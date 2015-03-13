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

import java.lang.reflect.Method;
import java.lang.annotation.Annotation;
import java.util.Vector;

import kenh.expl.*;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Provide the base implement for function sub class.
 * Sub class can use method <code>process(...)</code> or annotation <code>Processing</code>
 * to declare the function processing method.
 * 
 * @author Kenneth
 * @since 1.0
 *
 */
public abstract class BaseFunction implements Function {
	
	/**
	 * Logger
	 */
	protected static final Log logger = LogFactory.getLog(Function.class.getName());
	
	private Environment env = null;
	
	@Override
	public void setEnvironment(Environment env) {
		this.env = env;
	}
	
	@Override
	public Environment getEnvironment() {
		return env;
	}
	
	/**
	 * The default method name of processing
	 */
	private static final String METHOD = "process";
	
	/**
	 * Find the method with name <code>process</code> or <code>@Processing</code>
	 */
	@Override
	public Object invoke(Object... params) throws UnsupportedExpressionException {
		Method[] methods = this.getClass().getMethods();
		
		for(Method method: methods) {
			String name = method.getName();
			Class[] classes = method.getParameterTypes();
			Annotation a = method.getAnnotation(Processing.class);
			
			if((name.equals(METHOD) || a != null) && params.length == classes.length) {
				
				logger.trace("Method: " + method.toGenericString());
				boolean find = true;
				Object[] objs = new Object[params.length];
				for(int i=0; i< params.length; i++) {
					Class class1 = params[i].getClass();
					Class class2 = classes[i];
					
					if(class2.isAssignableFrom(class1) || class2 == Object.class) {
						objs[i] = params[i];
						
					} else if(class1 == String.class) {
						try {
							Object obj = Environment.convert((String)params[i], class2);
							if(obj == null) {
								logger.trace("Failure(Convert failure[" + (i+1) + "-" + class1 + "," + class2 + "]): " + method.toGenericString());
								find = false;
								break;
							} else {
								objs[i] = obj;
							}
						} catch(Exception e) {
							logger.trace("Failure(Convert exception[" + (i+1) + "-" + e.getMessage() + "]): " + method.toGenericString());
							find = false;
							break;
							//UnsupportedExpressionException ex = new UnsupportedExpressionException(e);
							//throw ex;
						}
					} else {
						logger.trace("Failure(Class unmatched[" + (i+1) + "]): " + method.toGenericString());
						find = false;
						break;
					}
				}
				if(find) {
					try {
						return method.invoke(this, objs);
					} catch(Exception e) {
						if(e instanceof UnsupportedExpressionException) throw (UnsupportedExpressionException)e;
						else throw new UnsupportedExpressionException(e);
					}
				}
			}
		}
		
		String paramStr = "";
		for(Object param: params) {
			paramStr += param.getClass().getCanonicalName() + ", ";
		}
		paramStr = StringUtils.defaultIfBlank(StringUtils.chop(StringUtils.trimToEmpty(paramStr)), "<NO PARAM>");
		
		UnsupportedExpressionException e = new UnsupportedExpressionException("Can't find the method to process.[" + paramStr + "]");
		throw e;
	}
	
}
