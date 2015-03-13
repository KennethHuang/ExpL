package junit.kenh.expl;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.hasItems;
import junit.kenh.expl.beans.TestObject;

import org.hamcrest.Matcher;
import org.junit.*;
import org.junit.rules.ExternalResource;

import kenh.expl.*;

/**
 * Test for ExpL environment
 * 
 * @author Kenneth
 *
 */
public class EnvironmentTest {
	
	private static kenh.expl.Environment env = null;
	
	@ClassRule
	public static ExternalResource resource= new ExternalResource() {
		@Override
		protected void before() throws Throwable {
			env = new kenh.expl.Environment();
	    };
	    
	    @Override
	    protected void after() {
	    	env.callback();
	    };
	};
	
	@Test
	// kenh.expl.Environment.setFunctionPackage(String key, String funcPackage)
	// kenh.expl.Environment.removeFunctionPackage(String key, String funcPackage)
	// kenh.expl.Environment.getFunction(String key, String funcName)
	// kenh.expl.Environment.getFunction(String funcName)
	public void testFunctions() {
		int i = 1;
		
		Assert.assertFalse("" + i++, env.setFunctionPackage(null, "junit.kenh.expl.functions.a"));
		Assert.assertFalse("" + i++, env.setFunctionPackage(null, null));
		
		Assert.assertFalse("" + i++, env.setFunctionPackage("expl", "junit.kenh.expl.functions.a"));
		Assert.assertTrue("" + i++, env.setFunctionPackage("test", "junit.kenh.expl.functions.a"));
		Assert.assertTrue("" + i++, env.setFunctionPackage("test", "junit.kenh.expl.functions.a"));
		Assert.assertFalse("" + i++, env.setFunctionPackage("test", "junit.kenh.expl.functions.b"));
		
		Assert.assertFalse("" + i++, env.removeFunctionPackage("test", "junit.kenh.expl.functions.b"));
		Assert.assertTrue("" + i++, env.removeFunctionPackage("test", "junit.kenh.expl.functions.a"));
		Assert.assertTrue("" + i++, env.setFunctionPackage("test", "junit.kenh.expl.functions.b"));
		Assert.assertTrue("" + i++, env.removeFunctionPackage("test", "junit.kenh.expl.functions.b"));
		
		Assert.assertTrue("" + i++, env.setFunctionPackage("test1", "junit.kenh.expl.functions.a"));
		Assert.assertTrue("" + i++, env.setFunctionPackage("test2", "junit.kenh.expl.functions.b"));
		
		Assert.assertThat("" + i++, "junit.kenh.expl.functions.a.GetString", equalTo(env.getFunction("test1", "getString").getClass().getCanonicalName()));
		Assert.assertThat("" + i++, "junit.kenh.expl.functions.b.GetString", equalTo(env.getFunction("test2", "getString").getClass().getCanonicalName()));
		Assert.assertThat("" + i++, "junit.kenh.expl.functions.a.GetString", equalTo(env.getFunction("getString").getClass().getCanonicalName()));
		Assert.assertThat("" + i++, "junit.kenh.expl.functions.b.GetString2", equalTo(env.getFunction("getString2").getClass().getCanonicalName()));
		
		Assert.assertNull("" + i++, env.getFunction("GETSTRING"));
	}
	
	
	@Test
	// kenh.expl.Environment.getVariables()
	// kenh.expl.Environment.getVariable(String key)
	// kenh.expl.Environment.setVariable(String key, Object obj)
	// kenh.expl.Environment.removeVariable(String key)
	// kenh.expl.Environment.removeVariable(String key, boolean callback)
	// kenh.expl.Environment.containsVariable(String key)
	// kenh.expl.Environment.containsValue(Object value)
	// kenh.expl.Callback.callback()
	public void testVariables() {
		int i = 1;
		
		env.setVariable("abc", "ABC");
		env.setVariable("def", "DEF");
		env.setVariable("ghi", "GHI");
		
		Assert.assertEquals("" + i++, 3, env.getVariables().size());
		Assert.assertThat("" + i++, env.getVariables().keySet(), hasItems("abc", "def", "ghi"));
		
		Assert.assertSame("" + i++, "ABC", env.getVariable("abc"));
		Assert.assertSame("" + i++, "DEF", env.getVariable("def"));
		Assert.assertSame("" + i++, "GHI", env.getVariable("ghi"));
		
		Assert.assertTrue("" + i++, env.containsVariable("abc"));
		Assert.assertTrue("" + i++, env.containsValue("ABC"));
		
		Assert.assertTrue("" + i++, env.containsVariable("ghi"));
		Assert.assertFalse("" + i++, env.containsVariable("ghi+"));
		Assert.assertTrue("" + i++, env.containsValue("GHI"));
		
		env.setVariable("ghi", "GHI+");
		Assert.assertSame("" + i++, "GHI+", env.getVariable("ghi"));
		Assert.assertTrue("" + i++, env.containsVariable("ghi"));
		Assert.assertFalse("" + i++, env.containsValue("GHI"));
		
		Assert.assertNull("" + i++, env.getVariable("abcdefghi"));
		
		TestObject obj = new TestObject();
		env.setVariable("eObj", obj);
		Assert.assertEquals("" + i++, TestObject.UNKNOWN, obj.getState());
		
		env.removeVariable("eObj", false);
		Assert.assertEquals("" + i++, TestObject.UNKNOWN, obj.getState());
		
		env.setVariable("eObj", obj);
		env.removeVariable("eObj");
		Assert.assertEquals("" + i++, TestObject.CALLBACK, obj.getState());
		
		TestObject obj_ = new TestObject();
		env.setVariable("tObj", obj_);
		Assert.assertEquals("" + i++, TestObject.UNKNOWN, obj_.getState());
		env.setVariable("tObj", "AAA");
		Assert.assertEquals("" + i++, TestObject.CALLBACK, obj_.getState());
		
	}
	
	@Test
	// kenh.expl.Environment.loadFunctionPackages_SystemProperties()
	public void testLoadFunctionPackages() {
		int i = 1;
		
		System.setProperty("kenh.expl.function.packages.test", "junit.kenh.expl.functions.a");
		kenh.expl.Environment env = new kenh.expl.Environment();
		
		Assert.assertThat("" + i++, "junit.kenh.expl.functions.a.GetString", equalTo(env.getFunction("getString").getClass().getCanonicalName()));
	}
	
	@Test
	// kenh.expl.Environment.convert(String s, Class c)
	public void testConvert() {
		int i = 1;
		
		Assert.assertThat("" + i++, 7, equalTo(kenh.expl.Environment.convert("7", int.class)));
		Assert.assertThat("" + i++, 120, equalTo(kenh.expl.Environment.convert("0120", Integer.class)));
		
		Assert.assertThat("" + i++, true, equalTo(kenh.expl.Environment.convert("true", boolean.class)));
		Assert.assertThat("" + i++, false, equalTo(kenh.expl.Environment.convert("non-true string", Boolean.class)));
		
		Assert.assertThat("" + i++, 11.17f, equalTo(kenh.expl.Environment.convert("11.17", float.class)));
		Assert.assertThat("" + i++, 1.03, equalTo(kenh.expl.Environment.convert("1.03", Double.class)));
		
		try {
			kenh.expl.Environment.convert("A", int.class);
			Assert.fail("" + i++);
		} catch(NumberFormatException e) {
			
		}
		
		Assert.assertSame("" + i++, "ABCDEFG", kenh.expl.Environment.convert("ABCDEFG", String.class));
		Assert.assertNull("" + i++, kenh.expl.Environment.convert("Nooooo", Object.class));
		Assert.assertNull("" + i++, kenh.expl.Environment.convert(null, String.class));
	}
	
	@Test
	// kenh.expl.Environment.callback()
	// kenh.expl.Callback.callback()
	public void testCallback() {
		int i = 1;
		
		kenh.expl.Environment env = new kenh.expl.Environment();
		TestObject obj = new TestObject();
		
		env.setVariable("ONE", obj);
		env.setVariable("TWO", new Object());
		
		Assert.assertEquals("" + i++, 2, env.getVariables().size());
		Assert.assertEquals("" + i++, TestObject.UNKNOWN, obj.getState());
		
		env.callback();
		Assert.assertEquals("" + i++, 0, env.getVariables().size());
		Assert.assertEquals("" + i++, TestObject.CALLBACK, obj.getState());
		
	}
	
}
