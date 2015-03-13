package junit.kenh.expl.beans;

/**
 * 
 * @author Kenneth
 *
 */
public class TestObject implements kenh.expl.Callback {
	
	public final static String UNKNOWN = "UNKNOWN";
	public final static String CALLBACK = "CALLBACK";
	
	private String state = UNKNOWN;
	
	public TestObject() { }
	
	public TestObject(String s) {
		setState(s);
	}
	
	protected void setState(String s) {
		state = s;
	}
	
	public String getState() {
		return state;
	}

	@Override
	public void callback() {
		state = CALLBACK;
	}
	
}
