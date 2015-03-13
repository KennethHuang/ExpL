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

/**
 * The parser interface of ExpL.
 * @author Kenneth
 * @since 1.0
 *
 */
public interface Parser {
	
	/**
	 * Parse the expression.
	 * @param express   The expression.
	 * @return
	 * @see Environment#parse(String)
	 */
	public Object parse(String express) throws UnsupportedExpressionException;
	
	/**
	 * Split the expression, and parse each.
	 * @param express
	 * @param split
	 * @return
	 * @throws UnsupportedExpressionException
	 */
	public Object[] parse(String express, boolean split) throws UnsupportedExpressionException;
	
	/**
	 * Set the environment.
	 * @param env
	 * @see Environment
	 */
	void setEnvironment(Environment env);
	
	/**
	 * Get the environment.
	 * @return
	 * @see Environment
	 */
	Environment getEnvironment();
	
	
}