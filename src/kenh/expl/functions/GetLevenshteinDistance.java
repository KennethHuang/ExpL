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
 * Find the Levenshtein distance between two Strings if it's less than or equal to a given
 * threshold. 
 * 
 * @author Kenneth
 * @since 1.0
 * 
 * @see StringUtils#getLevenshteinDistance(CharSequence, CharSequence)
 * @see StringUtils#getLevenshteinDistance(CharSequence, CharSequence, int)
 *
 */
public class GetLevenshteinDistance extends BaseFunction {
	
	public int process(String str1, String str2) {
		try {
			return StringUtils.getLevenshteinDistance(str1, str2);
		} catch(Exception e) {
			return 0;
		}
	}
	
	public int process(String str1, String str2, int threshold) {
		if(threshold < 0) threshold = 0;
		
		try {
			return StringUtils.getLevenshteinDistance(str1, str2, threshold);
		} catch(Exception e) {
			return 0;
		}
	}
}
