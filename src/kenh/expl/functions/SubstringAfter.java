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
 * Gets the substring after a separator.
 * 
 * @author Kenneth
 * @since 1.0
 * 
 * @see StringUtils#substringAfter(String, String)
 * @see StringUtils#substringAfterLast(String, String)
 *
 */
public class SubstringAfter extends BaseFunction {
	
	public String process(String str, String tag) {
		return process(str, tag, false);
	}
	
	public String process(String str, String open, boolean last) {
		if(last) return StringUtils.substringAfterLast(str, open);
		else return StringUtils.substringAfter(str, open);
	}
}
