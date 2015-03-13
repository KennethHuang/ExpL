package junit.kenh.expl.functions.b;

import kenh.expl.impl.BaseFunction;

public class GetString extends BaseFunction {
	
	private String suffix = "+";
	
	public String process() {
		return suffix;
	}
	
	public String process(boolean b) {
		return null;
	}

	public String process(int i) {
		return " ";
	}
}
