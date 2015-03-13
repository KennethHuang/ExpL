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

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import kenh.expl.*;

/**
 * Provide the base method for sub parser.
 * 
 * @author Kenneth
 * @since 1.0
 *
 */
public abstract class BaseParser implements Parser {
	
	private Environment env = null;
	
	/**
	 * Logger
	 */
	protected static final Log logger = LogFactory.getLog(Parser.class.getName());
	
	@Override
	public void setEnvironment(Environment env) {
		this.env = env;
	}

	@Override
	public Environment getEnvironment() {
		return env;
	}
	
	/**
	 * Convert to string
	 * @param obj
	 * @return
	 * @throws UnsupportedExpressionException
	 */
	protected String convertToString(Object obj) throws UnsupportedExpressionException {
		if (obj == null) return "";
		
		if (obj instanceof String) return (String)obj;
		if (obj instanceof Boolean) return "" + ((Boolean)obj).booleanValue();
		if (obj instanceof Integer) return "" + ((Integer)obj).intValue();
		if (obj instanceof Double) return "" + ((Double)obj).doubleValue();
		if (obj instanceof Float) return "" + ((Float)obj).floatValue();
		if (obj instanceof Long) return "" + ((Long)obj).longValue();
		if (obj instanceof Short) return "" + ((Short)obj).shortValue();
		if (obj instanceof Byte) return "" + ((Byte)obj).byteValue();
		
		/*
		if (obj.getClass().isArray()) {
			Object[] array = (Object[])obj;
			String result = "";
			for (int i = 0; i < array.length; i++) {
				String str = convertToString(array[i]);
				if (StringUtils.isBlank(result)) {
					result = str;
				} else {
					result = result + "," + str;
				}
			}
			
			return result;
		}
		*/
		
		throw new UnsupportedExpressionException("Unsupported [" + obj.getClass().getCanonicalName() + "]");
	}
	
	@Override
	public Object[] parse(String express, boolean split) throws UnsupportedExpressionException {
		throw new UnsupportedExpressionException("Do not supported this method.");
	}
	
}
