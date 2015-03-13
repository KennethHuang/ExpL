package junit.kenh.expl.functions.a;

import junit.kenh.expl.beans.TestObject;
import kenh.expl.Environment;
import kenh.expl.Processing;
import kenh.expl.Function;
import kenh.expl.UnsupportedExpressionException;

public class GetParameters implements Function {

	private Environment env = null;
	
	@Override
	public Object[] invoke(Object... params)
			throws UnsupportedExpressionException {
		return params;
	}

	@Override
	public void setEnvironment(Environment env) {
		this.env = env;
	}

	@Override
	public Environment getEnvironment() {
		return env;
	}
	

}
