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
 * Check if a String ends with any of an array of specified strings.
 * 
 * @author Kenneth
 * @since 1.0
 * 
 * @see StringUtils#endsWithAny(CharSequence, CharSequence...)
 *
 */
public class EndsWithAny extends BaseFunction {
	
	public boolean process(String str, String searchStrings1, String searchStrings2) {
		return StringUtils.endsWithAny(str, searchStrings1, searchStrings2);
	}
	
	public boolean process(String str, String searchStrings1, String searchStrings2, String searchStrings3) {
		return StringUtils.endsWithAny(str, searchStrings1, searchStrings2, searchStrings3);
	}
	
	public boolean process(String str, String searchStrings1, String searchStrings2, String searchStrings3, String searchStrings4) {
		return StringUtils.endsWithAny(str, searchStrings1, searchStrings2, searchStrings3, searchStrings4);
	}
	
	public boolean process(String str, String[] searchStrings) {
		return StringUtils.endsWithAny(str, searchStrings);
	}
	
}
