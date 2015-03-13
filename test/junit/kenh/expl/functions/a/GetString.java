package junit.kenh.expl.functions.a;

import junit.kenh.expl.beans.TestObject;
import kenh.expl.Processing;
import kenh.expl.impl.BaseFunction;

public class GetString extends BaseFunction {
	
	private String suffix = "_";
	
	public String process() {
		return suffix;
	}
	
	@Processing
	public String useAnnotationProcessing(String s) {
		if(s == null) return suffix;
		return s + suffix;
	}
	
	public String process(TestObject obj) {
		if(obj == null) return suffix;
		return obj.getState() + suffix;
	}
	
	public String process(String a, String b) {
		return a + suffix + b + suffix;
	}
	
}
