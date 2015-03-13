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

import java.util.*;
import org.apache.commons.lang3.StringUtils;

import kenh.expl.impl.BaseFunction;
import kenh.expl.Processing;

/**
 * Checks if string contains a search string.
 * 
 * @author Kenneth
 * @since 1.0
 * 
 * @see StringUtils#contains(CharSequence, CharSequence)
 * @see StringUtils#containsIgnoreCase(CharSequence, CharSequence)
 *
 */
public class Contains extends BaseFunction {
	
	public boolean process(String seq, String searchSeq) {
		return process(seq, searchSeq, false);
	}
	
	public boolean process(String seq, String searchSeq, boolean ignoreCase) {
		if(ignoreCase) return StringUtils.containsIgnoreCase(seq, searchSeq);
		else return StringUtils.contains(seq, searchSeq);
	}
}
