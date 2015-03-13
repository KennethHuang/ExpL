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

package kenh.expl.functions;

import org.apache.commons.lang3.StringUtils;
import kenh.expl.impl.BaseFunction;

/**
 * Compares two CharSequences, returning {@code true} if they represent equal sequences of characters.
 * 
 * @author Kenneth
 * @since 1.0
 * 
 * @see StringUtils#equals(CharSequence, CharSequence)
 * @see StringUtils#equalsIgnoreCase(CharSequence, CharSequence)
 *
 */
public class Equals extends BaseFunction {
	
	public boolean process(String s1, String s2) {
		return process(s1, s2, false);
	}
	
	public boolean process(String s1, String s2, boolean ignoreCase) {
		if(ignoreCase) return StringUtils.equalsIgnoreCase(s1, s2);
		else return StringUtils.equals(s1, s2);
	}
	
}
