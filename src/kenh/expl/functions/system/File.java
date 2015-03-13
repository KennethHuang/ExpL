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

package kenh.expl.functions.system;

import java.io.*;
import org.apache.commons.lang3.StringUtils;
import kenh.expl.UnsupportedExpressionException;
import kenh.expl.impl.BaseFunction;

/**
 * To get the File reference.
 * 
 * @author Kenneth
 * @since 1.0
 * 
 * @see java.io.File#File(String)
 * @see java.io.File#File(String, String)
 * 
 */
public class File extends BaseFunction {
	
	public java.io.File process(String path) throws IOException {
		java.io.File f = new java.io.File(path);
		f = f.getCanonicalFile();
		return f;
	}

	public java.io.File process(String path, String file) throws IOException {
		java.io.File f = new java.io.File(path, file);
		f = f.getCanonicalFile();
		return f;
	}
	
}
