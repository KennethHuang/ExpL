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

import java.io.PrintStream;
import java.util.Stack;

/**
 * The exception of ExpL.
 * 
 * @author Kenneth
 * @since 1.0
 *
 */
public class UnsupportedExpressionException extends Exception {
	
	private Stack<String> stack = new Stack();
	
    public UnsupportedExpressionException() {
        super();
    }
    
    public UnsupportedExpressionException(String message) {
        super(message);
    }
    
    public UnsupportedExpressionException(Throwable t) {
        super(t);
    }
    
    
    /**
     * Pushes an expression onto the top of this stack.
     * @param express   The expression
     */
    public void push(String express) {
    	stack.push(express);
    }
    
    /**
     * Removes the expression at the top of this stack.
     * @return
     */
    public String pop() {
    	if(stack.size() <= 0) return null;
    	
    	return stack.pop();
    }
    
    /**
     * Prints the expression backtrace to the specified print stream.
     * @param stream
     */
    public void printExpressTrace(PrintStream stream) {
    	if(stack.size() <= 0) return;
    	if(stream == null) stack.clear();
    	
    	if(this.getMessage() != null) {
    		stream.println("Exception: " + this.getMessage());
    	}
    	
    	stream.println("Express:");
    	String s = null;
    	while((s = pop()) != null) {
    		stream.println(s);
    	}
    }
    
    /**
     * Prints the expression backtrace
     */
    public void printExpressTrace() {
    	printExpressTrace(System.err);
    }
}
