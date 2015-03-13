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

package kenh.expl;

import java.util.*;

import kenh.expl.impl.ExpLParser;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Environment for ExpL. 
 * 
 * @author Kenneth
 * @since 1.0
 *
 */
public class Environment implements Callback {
	
	/**
	 * Map for variable
	 */
	private Map<String, Object> variables = Collections.synchronizedMap(new LinkedHashMap());
	
	/**
	 * Map for function package, let Environment know where to load function.
	 */
	private Map<String, String> functionPackages =  Collections.synchronizedMap(new LinkedHashMap());
	
	/**
	 * Function package loaded by system properties should use this prefix 
	 */
	private static final String FUNCTION_PATH_PREFIX = "kenh.expl.function.packages";
	
	/**
	 * The parser
	 */
	private Parser parser = null;
	
	/**
	 * Logger
	 */
	protected static final Log logger = LogFactory.getLog(Environment.class.getName());
	
	
	/**
	 * Constructor
	 */
	public Environment() {
		this(null);
	}
	
	/**
	 * Constructor
	 * @param parser  The parser
	 */
	public Environment(Parser parser) {
		if(parser == null) parser = new ExpLParser();
		
		setParser(parser);		
		setFunctionPackage("expl", "kenh.expl.functions");  // default function package
		
		loadFunctionPackages_SystemProperties();
		loadFunctionPackages_Extension();
		//setVariable("@system", System.getProperties());
	}
	
	/**
	 * Set the parser, and the parser's environment will be set.
	 * @param p   The parser
	 * @see Parser#setEnvironment(Environment)
	 */
	public void setParser(Parser parser) {
		this.parser = parser;
		parser.setEnvironment(this);
	}
	
	/**
	 * Parse the express. If the parser is null, then return null.
	 * @param express  The expression
	 * @return   
	 */
	public Object parse(String express) throws UnsupportedExpressionException {
		if(parser == null) return null;
		return parser.parse(express);
	}
	
	/**
	 * Parse the express. If the parser is null, then return null.
	 * @param express  The expression
	 * @return   
	 */
	public Object[] parse(String express, boolean split) throws UnsupportedExpressionException {
		if(parser == null) return null;
		return parser.parse(express, split);
	}
	
	/**
	 * Load function packages through system properties
	 */
	private void loadFunctionPackages_SystemProperties() {
		Properties p = System.getProperties();
		Set keys = p.keySet();
		for(Object key_: keys) {
			if(key_ instanceof String) {
				String key = (String)key_;
				if(StringUtils.startsWith(key, FUNCTION_PATH_PREFIX + ".")) {
					String name = StringUtils.substringAfter(key, FUNCTION_PATH_PREFIX + ".");
					String funcPackage = p.getProperty(key);
					setFunctionPackage(name, funcPackage);
				}
			}
		}
	}
	
	/**
	 * Load function package through <code>Extension</code>.
	 * @see Extension
	 */
	private void loadFunctionPackages_Extension() {
		ServiceLoader<Extension> es = ServiceLoader.load(Extension.class);
		for(Extension e: es) {
			if(e!= null) {
				Map<String, String> p = e.getFunctionPackages();
				if(p != null && p.size() > 0) {
					Set<String> keys = p.keySet();
					for(String key: keys) {
						String funcPackage = p.get(key);
						setFunctionPackage(key, funcPackage);
					}
				}
			}
		}
	}
	
	/**
	 * Load <code>Function</code>. 
	 * 
	 * @param funcPackage  The function package.
	 * @param funcName     The function name.
	 * @return      Use <code>funcPackage + funcName</code> to load class, if not find, return null.
	 */
	private Function getFunction_(String funcPackage, String funcName) {
		if(StringUtils.isBlank(funcPackage)) return null;
		if(StringUtils.isBlank(funcName)) return null;
		
		funcPackage = StringUtils.trimToEmpty(funcPackage);
		funcName = StringUtils.trimToEmpty(funcName);
		
		try {
			if(StringUtils.isNotBlank(funcPackage)) {
				Function function = (Function)Class.forName(funcPackage + "." + StringUtils.capitalize(funcName)).newInstance();
				return function;
			}
		} catch(Throwable e) {
		}
		return null;
	}
	
	/**
	 * Loop all function packages, find the first class match the function name.
	 * @param funcName   The function name.
	 * @return
	 */
	public Function getFunction(String funcName) {
		if(StringUtils.isBlank(funcName)) return null;
		
		funcName = StringUtils.trimToEmpty(funcName);
		
		Iterator<String> iterator = functionPackages.values().iterator();
		while(iterator.hasNext()) {
			String funcPackage = iterator.next();
			Function function = getFunction_(funcPackage, funcName);
			if(function != null) return function;
		}
		return null;
	}
	
	/**
	 * Load <code>Function</code> through name space.
	 * @param key       The key of function package 
	 * @param funcName  The function name
	 * @return
	 */
	public Function getFunction(String key, String funcName) {
		if(StringUtils.isBlank(funcName)) return null;
		
		key = StringUtils.trimToEmpty(key);
		funcName = StringUtils.trimToEmpty(funcName);
		
		if(StringUtils.isNotBlank(key)) {
			if(functionPackages.containsKey(key)) {
				return getFunction_(functionPackages.get(key), funcName);
			}
			return null;
		} else {
			return getFunction(funcName);
		}
		
	}
	
	/**
	 * Add a function package
	 * @param key     The key of function package
	 * @param funcPackage
	 * @return   If the key already exist, but function package is not the same, then return false.
	 * @see Map#put(Object, Object)
	 */
	public boolean setFunctionPackage(String key, String funcPackage) {
		if(StringUtils.isBlank(key)) return false;
		if(StringUtils.isBlank(funcPackage)) return false;
		
		key = StringUtils.trimToEmpty(key);
		funcPackage = StringUtils.trimToEmpty(funcPackage);
		
		if(functionPackages.containsKey(key)) {
			String p = functionPackages.get(key);
			if(p.equals(funcPackage)) return true;
			else return false;
			
		} else {
			functionPackages.put(key, funcPackage);
			return true;
		}
	}
	
	/**
	 * Remove a function package.
	 * @param key    The key of function package
	 * @param funcPackage
	 * @return   true, if both key and function package are matching.
	 */
	public boolean removeFunctionPackage(String key, String funcPackage) {
		if(StringUtils.isBlank(key)) return false;
		if(StringUtils.isBlank(funcPackage)) return false;
		
		key = StringUtils.trimToEmpty(key);
		funcPackage = StringUtils.trimToEmpty(funcPackage);
		
		if(functionPackages.containsKey(key)) {
			String p = functionPackages.get(key);
			if(p.equals(funcPackage)) {
				functionPackages.remove(key);
				return true;
				
			} else return false;
			
		} else {
			return true;
		}
	}
	
	/**
	 * Get all variables.
	 * @return
	 */
	public Map<String, Object> getVariables() {
		return variables;
	}
	
	/**
	 * Get the specified variable.
	 * @param key  The key of variable.
	 * @return
	 */
	public Object getVariable(String key) {
		if(StringUtils.isBlank(key)) return null;
		return variables.get(key);
	}
	
	/**
	 * Add a variable, if the key already exist, remove the old variable.
	 * @param key
	 * @param obj
	 */
	public void setVariable(String key, Object obj) {
		if(StringUtils.isBlank(key)) return;
		
		if (variables.containsKey(key)) {
			Object oldObj = variables.remove(key);
			callback(oldObj);
			variables.put(key, obj);
			logger.debug("Replace var: " + key + ", " + ((obj == null)?"<null>":obj.toString()));
		} else {
			variables.put(key, obj);
			logger.debug("Add var: " + key + ", " + ((obj == null)?"<null>":obj.toString()));
		}
	}
	
	/**
	 * Remove the variable, if variable implement <code>Callback</code>, call back method will be called.
	 * @param key  The key of variable.
	 * @return
	 */
	public Object removeVariable(String key) {
		return removeVariable(key, true);
	}
	
	/**
	 * Remove the variable.
	 * @param key
	 * @param callback  if true and variable implement <code>Callback</code>, call back method will be called.
	 * @return
	 */
	public Object removeVariable(String key, boolean callback) {
		if(StringUtils.isBlank(key)) return null;
		if (!variables.containsKey(key)) return null;
		
		Object oldObj = variables.remove(key);
		boolean c = false;
		if(callback) {
			callback(oldObj);
			c = true;
		}
		if(c) {
			logger.debug("Remove var(C): " + key);
		} else {
			logger.debug("Remove var: " + key);
		}
		return oldObj;
	}
	
	/**
	 * Check if contains the variable with specified key.
	 * @param key  The key of variable.
	 * @return
	 */
	public boolean containsVariable(String key) {
		return variables.containsKey(key);
	}
	
	/**
	 * Check if contains the specified object.
	 * @param value   The object.
	 * @return
	 */
	public boolean containsValue(Object value) {
		return variables.containsValue(value);
	}
	
	/**
	 * Invoke the call back method.
	 * @param obj    The object implement <code>Callback</code>.
	 * @see Callback
	 */
	protected void callback(Object obj) {
		if (obj instanceof Callback) {
			((Callback)obj).callback();
		}
	}
	
	@Override
	public void callback() {
		Iterator<String> iterator = variables.keySet().iterator();
		while(iterator.hasNext()) {
			String key = iterator.next();
			Object obj = variables.get(key);
			callback(obj);
		}
		variables.clear();
	}
	
	/**
	 * Convert string to specified type. Only support primitive type.
	 * @param s
	 * @param c
	 * @return  if s is null, return null.
	 */
	public static Object convert(String s, Class c) {
		if(s == null) return null;
		if(c == String.class) return s;
		
		if(c == int.class || c == Integer.class) {
			return Integer.parseInt(s);
		}
		if(c == boolean.class || c == Boolean.class) {
			return Boolean.parseBoolean(s);
		}
		if(c == float.class || c == Float.class) {
			return Float.parseFloat(s);
		}
		if(c == double.class || c == Double.class) {
			return Double.parseDouble(s);
		}
		return null;
	}
	
	// Demo method
	public static void main(String[] args) {
		Environment env = new Environment();
		
		// 1) normal operation
		try {
			System.out.println(env.parse("7 % 4 = {7 mod 4}"));
			System.out.println(env.parse("1 + 1 = {1 + 1}"));
			System.out.println(env.parse("1 + 1 + 1 = {1 + {1 + 1}}"));
		} catch(UnsupportedExpressionException e) {
			e.printExpressTrace();
		}
		
		System.out.println("---------------");
		
		// 2) using variable;
		env.setVariable("var1", "2");
		env.setVariable("var2", "3");
		env.setVariable("var3", "4");
		env.setVariable("var4", new Date());
		env.setVariable("var5", "{1+5}");
		env.setVariable("var6", "{var5}");
		try {
			System.out.println(env.parse("$var2 = {$var{$var1}}"));
			System.out.println(env.parse("$var3 = {$var{$var{$var1}}}"));
			System.out.println(env.parse("{$var{$var{$var{$var1}}}}"));
			System.out.println(env.parse("$var5 = {$var5}"));
			System.out.println(env.parse("$var6 = {$var{{$var5}}}"));
		} catch(UnsupportedExpressionException e) {
			e.printExpressTrace();
		}
		
		System.out.println("---------------");
		
		// 3) using function
		try {
			System.out.println(env.parse("#contains = {#contains(123456789,{$var1})}"));
			System.out.println(env.parse("{#expl:split({'abc,def,ghi'},{','})}"));
		} catch(UnsupportedExpressionException e) {
			e.printExpressTrace();
			e.printStackTrace();
		}
		
		System.out.println("---------------");
		
		// 4) using JEXL
		try {
			System.out.println(env.parse("JEXT >> {var4.getDate()}"));
			System.out.println(env.parse("JEXT >> {'abc'}"));
			System.out.println(env.parse("{jextvar=['a', 'b', 'c']}"));
			System.out.println(env.parse("{jextvar}"));
		} catch(UnsupportedExpressionException e) {
			e.printExpressTrace();
			e.printStackTrace();
		}
		
		env.callback();
		
	}		
	
}