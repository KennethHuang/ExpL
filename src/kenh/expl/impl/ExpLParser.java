/*
 * ExpL (Expression Language)
 * Copyright 2014 and beyond, Kenneth Huang
 * 
 * This file is part of ExpL.
 * 
 * ExpL is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License version 3
 * as published by the Free Software Foundation.
 * 
 * ExpL is distributed in the hope that it will be useful, 
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public
 * License along with ExpL.  If not, see <http://www.gnu.org/licenses/>. 
 */

package kenh.expl.impl;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.JOptionPane;

import kenh.expl.*;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * The ExpL parser, support variable nesting, function
 * for example: {$var}，{$var_{$no}}，{#function1({$para1},2,{#function2()})}，{1+1} etc.
 * 
 * @author Kenneth
 * @since 1.0
 *
 */
public class ExpLParser extends BaseParser {
	
	/**
	 * the function name pattern, support name space.
	 */
	private static Pattern funcNamePattern = Pattern.compile("#[a-zA-Z]([a-zA-Z]|[0-9]|_)*(:[a-zA-Z]([a-zA-Z]|[0-9]|_)*)?");  // 支持Function命名空间
	
	/**
	 * Sub parser, the default is JEXLParser
	 */
	private Parser subParser = null; 
	
	
	/**
	 * Constructor
	 */
	public ExpLParser() {
		this(new JEXLParser());
	}
	
	/**
	 * Constructor
	 * @param parser   The sub parser
	 */
	public ExpLParser(Parser parser) {
		subParser = parser;
	}
	
	@Override
	public void setEnvironment(Environment env) {
		super.setEnvironment(env);
		if(subParser != null) subParser.setEnvironment(env);
	}
	
	@Override
	public Object parse(String express) throws UnsupportedExpressionException {
		Object obj = parseExpress(express);
		logger.info(express + " -> " + ((obj == null)?"<null>":obj.toString()));
		return obj;
	}
	
	@Override
	public Object[] parse(String express, boolean split) throws UnsupportedExpressionException {
		if (express == null) {
			UnsupportedExpressionException e = new UnsupportedExpressionException("Expression is null.");
			throw e;
		}
		
		if(split) {
			
			String[] parts = splitParameter(express);
			
			Object[] objs = new Object[parts.length];
			int i=0;
			for(String part: parts) {
				if (part.indexOf('{') != -1) {
					Object obj = this.parse(part);
					objs[i++] = obj;
				} else {
					objs[i++] = part;
				}
			}
			
			return objs;
			
		}  else {
			
			return new Object[] { this.parse(express) };
			
		}
		
	}
	
	/**
	 * Parse the expression.
	 * This method will looking for string in brace, and get the value back.
	 * @param express  The expression
	 * @return    Return string or non-string object. Return empty string instead of null.
	 */
	private Object parseExpress(String express) throws UnsupportedExpressionException {
		if (express == null) {
			UnsupportedExpressionException e = new UnsupportedExpressionException("Expression is null.");
			throw e;
		}
		
		logger.trace("Expr: " + express);
		
		int left = StringUtils.countMatches(express, "{");
		int right = StringUtils.countMatches(express, "}");
		
		if(left != right) {
			UnsupportedExpressionException ex = new UnsupportedExpressionException("'{' and '}' does not match.");
			ex.push(express);
			throw ex;
		}
		
		if(left == 0) return express;
		
		String sResult = null;	// String type result
		Object oResult = null;	// Non-string type result
		
		StringBuffer resultBuffer = new StringBuffer();
		
		try {
			String context = express;
			while(!context.equals("")) {
				String[] s = splitExpression(context);
				if(!s[0].equals("")) resultBuffer.append(s[0]);
				if(!s[1].equals("")) {
					String variable = StringUtils.substring(s[1], 1, s[1].length()-1);
					Object obj = getVariableValue(variable);
					logger.debug(variable + " -> " + ((obj == null)?"<null>":obj.toString()));
					try {
						String str = convertToString(obj);
						resultBuffer.append(str);
					} catch(UnsupportedExpressionException e) {
						if(oResult == null) oResult = obj;
						else {
							UnsupportedExpressionException ex = new UnsupportedExpressionException("Multi non-string objects returned. [" + oResult.getClass().getCanonicalName() + ", " + obj.getClass().getCanonicalName() + "]");
							ex.push(express);
							throw ex;
						}
					}
				}
				context = s[2];
			}

			
		} catch(UnsupportedExpressionException e) {
			e.push(express);
			throw e;
		} catch(Exception e) {
			UnsupportedExpressionException ex = new UnsupportedExpressionException(e);
			ex.push(express);
			throw ex;
		}
		
		sResult = resultBuffer.toString();
		Object returnObj = null;
		if(StringUtils.isBlank(sResult)) {
			if(oResult != null) returnObj = oResult;
			else returnObj = sResult;
		} else {
			if(oResult != null) {
				UnsupportedExpressionException e = new UnsupportedExpressionException("Non-string object exist. [" + oResult.getClass().getCanonicalName() + "]");
				e.push(express);
				throw e;
			}
			else returnObj = sResult;
		}
		
		logger.debug(express + " -> " + ((returnObj == null)?"<null>":returnObj.toString()));
		return returnObj;
	}
	
	/**
	 * Get the value in brace.
	 * $      - it will be a variable in environment.
	 * #      - will be a function
	 * other  - use sub parser
	 * 
	 * @param variable  the string in brace.
	 * @return  Return string or non-string object. Return empty string instead of null.
	 * @throws UnsupportedExpressionException
	 */
	private Object getVariableValue(String variable) throws UnsupportedExpressionException {
		if (variable == null) {
			UnsupportedExpressionException e = new UnsupportedExpressionException("Variable is null.");
			throw e;
		}
		
		logger.trace("Var: " + variable);
		
		if (variable.startsWith("$")) {
			// $ - it will be a variable in environment.
			
			if (variable.indexOf('{') != -1) {
				Object obj = this.parseExpress(variable);
				if(obj instanceof String) {
					variable = (String)obj;
				} else {
					UnsupportedExpressionException ex = new UnsupportedExpressionException("Unable to get variable");
					ex.push(variable);
					throw ex;
				}
			}
			
			String name = variable.substring(1);
			int index = name.indexOf('.');
			if (index == -1) {
				return this.getEnvironment().getVariable(name);
				
			} else {
				// use BeanUtils
				String str = name.substring(index + 1);
				name = name.substring(0, index);
				Object obj = null;
				if ((obj = this.getEnvironment().getVariable(name))!=null) {
					try {
						return BeanUtils.getProperty(obj, str);
					} catch(Exception e) {
						return "";
					}
				} else {
					return "";
				}
			}
			
		} else if (variable.startsWith("#")) {
			
			// # - will be a function
			
			int left = StringUtils.countMatches(variable, "(");
			int right = StringUtils.countMatches(variable, ")");
			
			if(left != right) {
				UnsupportedExpressionException ex = new UnsupportedExpressionException("'(' and ')' does not match.");
				ex.push(variable);
				throw ex;
			}
			
			String funcName = StringUtils.substringBefore(variable, "(");
			String funcAfter = StringUtils.substringAfterLast(variable, ")");
			
			if(StringUtils.isNotBlank(funcAfter)) {
				UnsupportedExpressionException e = new UnsupportedExpressionException("Function parse error.");
				e.push(variable);
				throw e;
			}
			
			if(funcNamePattern.matcher(funcName).matches()) {
				Object result = executeFunction(variable);
				logger.debug(variable + " -> " + ((result == null)?"<null>":result.toString()));
				if(result == null) return "";
				return result;
			} else {
				UnsupportedExpressionException e = new UnsupportedExpressionException("Function name parse error.");
				e.push(variable);
				throw e;
			}
			
		} else {
			
			if (variable.indexOf('{') != -1) {
				Object obj = this.parseExpress(variable);
				if(obj instanceof String) {
					variable = (String)obj;
				} else {
					UnsupportedExpressionException ex = new UnsupportedExpressionException("Unable to get variable");
					ex.push(variable);
					throw ex;
				}
			}
			
			// use sub parser
			try {
				return subParser.parse(variable);
			} catch(UnsupportedExpressionException e) {
				e.push(variable);
				throw e;
			} catch(Exception e) {
				UnsupportedExpressionException ex = new UnsupportedExpressionException(e);
				ex.push(variable);
				throw ex;
			}
			
		}
	}
	
	
	/**
	 * Invoke functioin, support name space. For example, expl:contains(...).
	 * @param function
	 * @return  Return string or non-string object. Return empty string instead of null.
	 */
	private Object executeFunction(String function) throws UnsupportedExpressionException { // function should return a string object;
		
		if (StringUtils.isBlank(function)) {
			UnsupportedExpressionException e = new UnsupportedExpressionException("Function is null.");
			throw e;
		}
		
		logger.trace("Func: " + function);
		
		String parameter = StringUtils.substringBeforeLast(StringUtils.substringAfter(function, "("), ")");
		
		String[] parts = splitParameter(parameter);
		String funcName = StringUtils.substringBetween(function, "#", "(");
		String nameSpace = null;
		
		if(StringUtils.contains(funcName, ":")) {
			nameSpace = StringUtils.substringBefore(funcName, ":");
			funcName = StringUtils.substringAfter(funcName, ":");
		}
		
		if(StringUtils.isBlank(funcName)) {
			UnsupportedExpressionException e = new UnsupportedExpressionException("Failure to get function name");
			e.push(function);
			throw e;
		}
		
		Object[] objs = new Object[parts.length];
		int i=0;
		for(String part: parts) {
			if (part.indexOf('{') != -1) {
				Object obj = this.parseExpress(part);
				objs[i++] = obj;
			} else {
				objs[i++] = part;
			}
		}
		
		try {
			// instantiate it, and create the parameters
			Function func = this.getEnvironment().getFunction(nameSpace, funcName);
			if(func == null) throw new UnsupportedExpressionException("Can't find the function. [" + (StringUtils.isBlank(nameSpace)? funcName : nameSpace + ":" + funcName) + "]");
			
			func.setEnvironment(this.getEnvironment());
			
			// invoke
			return func.invoke(objs);
		} catch(UnsupportedExpressionException e) {
			e.push(function);
			throw e;
			
		} catch (Exception ex) {
			UnsupportedExpressionException e = new UnsupportedExpressionException(ex);
			e.push(function);
			throw e;
		}
	}
	
	/**
	 * Get the parameters for function, use ',' to split each parameter.
	 * @param parameter
	 * @return
	 */
	private static String[] splitParameter(String parameter) throws UnsupportedExpressionException {
		Vector<String> params = new Vector();
		
		StringBuffer param = new StringBuffer();
		int count = 0;
		for (int scan = 0; scan < parameter.length(); scan++) {
			char c = parameter.charAt(scan);
			
			if(c == ',') {
				if(count > 0) {
					param.append(c);
				} else {
					params.add(param.toString());
					param = new StringBuffer();
				}
			} else if(c == '{') {
				count++;
				param.append(c);
			} else if(c == '}') {
				count--;
				param.append(c);
				if(count < 0) {
					UnsupportedExpressionException e = new UnsupportedExpressionException("Missing '{'.");
					e.push(parameter);
					throw e;
				}
			} else {
				param.append(c);
			}
		}
		
		if(count > 0) {
			UnsupportedExpressionException e = new UnsupportedExpressionException("Missing '}'.");
			e.push(parameter);
			throw e;
		}
		
		String lastParam = param.toString();
		
		if(params.size() == 0 && lastParam.equals("")) {
			// '()' only, mean no parameter
		} else {
			params.add(lastParam);
		}
		
		return params.toArray(new String[] {});
	}
	
	/**
	 * Split the expression, get the first expression in brace.
	 * @return  return an array, length is 3. for example: abc{def}ghi, then return {abc, def, ghi}.
	 *          this array never return null, instead of blank.
	 */
	private static String[] splitExpression(String express) throws UnsupportedExpressionException {
		
		if(express == null) {
			UnsupportedExpressionException e = new UnsupportedExpressionException("Expression is null.");
			throw e;
		}
		
		StringBuffer one = new StringBuffer();
		StringBuffer two = new StringBuffer();
		StringBuffer three = new StringBuffer();
		
		if (express.indexOf('{') == -1)	one.append(express);
		else {
			int count = 0;
			int scan = 0;
			for (; scan < express.length(); scan++) {
				char c = express.charAt(scan);
				
				if(c == '{') {
					count++;
					two.append(c);
				} else if(c == '}') {
					count--;
					two.append(c);
					if(count < 0) {
						UnsupportedExpressionException e = new UnsupportedExpressionException("Missing '{'.");
						e.push(express);
						throw e;
					}
					if(count == 0) {
						scan++;
						break;
					}
				} else {
					if(count > 0) {
						two.append(c);
					} else {
						one.append(c);
					}
				}
			
			}
			
			if(count > 0) {
				UnsupportedExpressionException e = new UnsupportedExpressionException("Missing '}'.");
				e.push(express);
				throw e;
			}
			
			if(scan < express.length()) {
				for (; scan < express.length(); scan++) {
					three.append(express.charAt(scan));
				}
			}
		}
		
		return new String[] { one.toString(), two.toString(), three.toString() };
	}
	
	// Main method
	public static void main(String[] args) {
		
		// test splitExpression
		/*
		try {
			String[] ss = splitExpression("1 + {2} + 3");
			System.out.println(ArrayUtils.toString(ss));
		} catch(UnsupportedExpressionException e) {
			e.printExpressTrace();
		}
		try {
			String[] ss = splitExpression("1 + {{2 + 3}+4}+ 5");
			System.out.println(ArrayUtils.toString(ss));
		} catch(UnsupportedExpressionException e) {
			e.printExpressTrace();
		}
		try {
			String[] ss = splitExpression("1 + {{2 + 3}+4}+ 5} + 6");
			System.out.println(ArrayUtils.toString(ss));
		} catch(UnsupportedExpressionException e) {
			e.printExpressTrace();
		}
		try {
			String[] ss = splitExpression("1 + {{2 + {3}+4}+ 5");
			System.out.println(ArrayUtils.toString(ss));
		} catch(UnsupportedExpressionException e) {
			e.printExpressTrace();
		}
		System.exit(-1);
		*/
		// test splitParameter
		/*
		try {
			String[] ss = splitParameter("1 + {2 + {3}+4}+ 5, abc,e{f{g}}, {#hi({a},b,c)}jk,lmn,,,o,qpr");
			System.out.println(ss.length + ">> " + ArrayUtils.toString(ss));
		} catch(UnsupportedExpressionException e) {
			e.printExpressTrace();
		}
		try {
			String[] ss = splitParameter("1 + {2 + {3}+4}+ 5, abc,e{fg, {hi}jk,lmn,,,o,qpr");
			System.out.println(ss.length + ">> " + ArrayUtils.toString(ss));
		} catch(UnsupportedExpressionException e) {
			e.printExpressTrace();
		}
		try {
			String[] ss = splitParameter("1 + {2 + {3}+4}+ 5, abc,e{fg}}, {hi}jk,lmn,,,o,qpr");
			System.out.println(ss.length + ">> " + ArrayUtils.toString(ss));
		} catch(UnsupportedExpressionException e) {
			e.printExpressTrace();
		}
		System.exit(-1);
		*/
		
		Environment env = new Environment();
		
		while(true) {
			String express = javax.swing.JOptionPane.showInputDialog(null, "Expression: ", "EXPL", JOptionPane.PLAIN_MESSAGE);
			
			if(express == null) break;
			
			try {
				System.out.println(env.parse(express));
			} catch(Exception e) {
				e.printStackTrace();
			}
		}
		
		env.callback();
		
	}


}
