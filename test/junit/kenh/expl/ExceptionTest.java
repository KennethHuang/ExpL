package junit.kenh.expl;

import junit.kenh.expl.beans.AObject;
import junit.kenh.expl.beans.BObject;
import junit.kenh.expl.beans.CObject;
import kenh.expl.UnsupportedExpressionException;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.rules.ExternalResource;

public class ExceptionTest {
	
	private static kenh.expl.Environment env = null;
	
	@Rule
	public ExpectedException thrown = ExpectedException.none();

	@ClassRule
	public static ExternalResource resource= new ExternalResource() {
		@Override
		protected void before() throws Throwable {
			env = new kenh.expl.Environment();
			
			env.setFunctionPackage("a", "junit.kenh.expl.functions.a");
			env.setFunctionPackage("b", "junit.kenh.expl.functions.b");
			
			env.setVariable("aObj", new AObject("AA"));
			env.setVariable("bObj", new BObject("BB"));
			env.setVariable("cObj", new CObject("CC"));
	    };
	    
	    @Override
	    protected void after() {
	    	env.callback();
	    };
	};
	
	@Test
	public void test_Null_Expression1() throws UnsupportedExpressionException {
		thrown.expect(UnsupportedExpressionException.class);
		thrown.expectMessage("Expression is null.");
		
		env.parse(null);
	}
	
	@Test
	public void test_Null_Expression2() throws UnsupportedExpressionException {
		thrown.expect(UnsupportedExpressionException.class);
		thrown.expectMessage("Expression is null.");
		
		env.parse(null, true);
	}
	
	@Test
	public void test_Unmatch_Brace1() throws UnsupportedExpressionException {
		thrown.expect(UnsupportedExpressionException.class);
		thrown.expectMessage("Missing '{'.");
		
		env.parse("{{1+1} + 1}}", true);
	}
	
	@Test
	public void test_Unmatch_Brace2() throws UnsupportedExpressionException {
		thrown.expect(UnsupportedExpressionException.class);
		thrown.expectMessage("Missing '}'.");
		
		env.parse("{{{1+1} + 1}", true);
	}
	
	@Test
	public void test_Unmatch_Brace3() throws UnsupportedExpressionException {
		thrown.expect(UnsupportedExpressionException.class);
		thrown.expectMessage("'{' and '}' does not match.");
		
		env.parse("{'}}");
	}
	
	@Test
	public void test_Mix_String_Object() throws UnsupportedExpressionException {
		thrown.expect(UnsupportedExpressionException.class);
		thrown.expectMessage("Non-string object exist. [junit.kenh.expl.beans.AObject]");
		
		env.parse("{exception thrown, {aObj}}");
	}
	
	@Test
	public void test_Mix_Object_Object() throws UnsupportedExpressionException {
		thrown.expect(UnsupportedExpressionException.class);
		thrown.expectMessage("Multi non-string objects returned. [junit.kenh.expl.beans.AObject, junit.kenh.expl.beans.BObject]");
		
		env.parse("{aObj}{bObj}{cObj}");
	}
	
	@Test
	public void test_Function_Name_Parse1() throws UnsupportedExpressionException {
		thrown.expect(UnsupportedExpressionException.class);
		thrown.expectMessage("Function name parse error.");
		
		env.parse("{#a:doNotSupportUnderline_:a()}");
	}
	
	@Test
	public void test_Function_Name_Parse2() throws UnsupportedExpressionException {
		thrown.expect(UnsupportedExpressionException.class);
		thrown.expectMessage("Function name parse error.");
		
		env.parse("{#()}");
	}
	
	@Test
	public void test_Function_Name_Parse3() throws UnsupportedExpressionException {
		thrown.expect(UnsupportedExpressionException.class);
		thrown.expectMessage("Function name parse error.");
		
		env.parse("{#}");
	}
	
	@Test
	public void test_Find_Function1() throws UnsupportedExpressionException {
		thrown.expect(UnsupportedExpressionException.class);
		thrown.expectMessage("Can't find the function. [b:none]");
		
		env.parse("{#b:none()}");
	}
	
	@Test
	public void test_Find_Function2() throws UnsupportedExpressionException {
		thrown.expect(UnsupportedExpressionException.class);
		thrown.expectMessage("Can't find the function. [getString3]");
		
		env.parse("{#getString3()}");
	}
	
	@Test
	public void test_Find_Function3() throws UnsupportedExpressionException {
		thrown.expect(UnsupportedExpressionException.class);
		thrown.expectMessage("Can't find the function. [parent]");
		
		env.parse("{#parent()}");
	}
	
	@Test
	public void test_Find_Function_Method() throws UnsupportedExpressionException {
		thrown.expect(UnsupportedExpressionException.class);
		thrown.expectMessage("Can't find the method to process.[java.lang.String, java.lang.String, java.lang.String]");
		
		env.parse("{#getString(a,b,c)}");
	}
	
	@Test
	public void test_Get_Variable1() throws UnsupportedExpressionException {
		thrown.expect(NullPointerException.class);
		thrown.expectMessage("Can't find the variable. [abcdefg]");
		
		env.getVariable("abcdefg", "");
	}
	
	@Test
	public void test_Get_Variable2() throws UnsupportedExpressionException {
		thrown.expect(NullPointerException.class);
		thrown.expectMessage("Can't find the variable with specify class type. [abcdefg]");
		
		env.setVariable("abcdefg", new junit.kenh.expl.beans.AObject());
		env.getVariable("abcdefg", new junit.kenh.expl.beans.BObject());
	}
	
	@Test
	public void test_Get_Variable3() throws UnsupportedExpressionException {
		thrown.expect(NullPointerException.class);
		thrown.expectMessage("Can't find the variable with specify class type. [abcdefg]");
		
		env.setVariable("abcdefg", new junit.kenh.expl.beans.AObject());
		env.getVariable("abcdefg", junit.kenh.expl.beans.BObject.class);
	}
	
}
