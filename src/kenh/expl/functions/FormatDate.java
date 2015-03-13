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

import java.text.SimpleDateFormat;
import java.util.*;

import kenh.expl.impl.BaseFunction;

/**
 * Format a date to string or conver a string to date.
 * 
 * @author Kenneth
 * @since 1.0
 * 
 *
 */
public class FormatDate extends BaseFunction {
	
	public Date process(String date, String[] patterns) throws Exception {
		int i = 0;
		for(String pattern: patterns) {
			i++;
			try {
				SimpleDateFormat format = new SimpleDateFormat(pattern);
				return format.parse(date);
			} catch(Exception e) {
				if(i >= patterns.length) {
					throw e;
				}
			}
		}
		
		throw new NullPointerException("Missing pattern.");
	}
	
	public Date process(String date, String pattern) throws Exception {
		return process(date, new String[] { pattern });
	}
	
	public Date process(String date, String pattern1, String pattern2) throws Exception {
		return process(date, new String[] { pattern1, pattern2 });
	}
	
	public Date process(String date, String pattern1, String pattern2, String pattern3) throws Exception {
		return process(date, new String[] { pattern1, pattern2, pattern3 });
	}
	
	public String process(Date date, String pattern) {
		SimpleDateFormat format = new SimpleDateFormat(pattern);
		return format.format(date);
	}

}
