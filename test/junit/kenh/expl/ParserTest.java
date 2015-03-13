package junit.kenh.expl;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.anyOf;
import static org.hamcrest.CoreMatchers.hasItems;

import java.util.regex.Pattern;

import junit.kenh.expl.beans.*;

import org.hamcrest.Matcher;
import org.junit.*;
import org.junit.rules.ExternalResource;

import kenh.expl.*;

/**
 * Test for ExpL parser.
 * 
 * @author Kenneth
 *
 */
public class ParserTest {
	
	private static kenh.expl.Environment env = null;
	
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
			
			env.setVariable("normal", "abcdefg");
			env.setVariable(":use:", ":");
			env.setVariable("@use@", "@");
			env.setVariable("#use#", "#");
			env.setVariable("()use()", "()");
			env.setVariable("$use$", "$");
			env.setVariable("left_brace", "{");
			env.setVariable("right_brace", "}");
	    };
	    
	    @Override
	    protected void after() {
	    	env.callback();
	    };
	};
	
	@Test
	public void testParse() throws Throwable {
		Assert.assertEquals("", env.parse("{$}"));
		
		Object[] objs = env.parse("{{'a'}Obj},{bObj},'cObj'", true);
		Assert.assertEquals(3, objs.length);
		Assert.assertEquals(env.getVariable("aObj"), objs[0]);
		Assert.assertEquals(env.getVariable("bObj"), objs[1]);
		Assert.assertEquals("'cObj'", objs[2]);
	}
	
	@Test
	public void testVariable() throws Throwable {
		Assert.assertEquals("AA", env.parse("{aObj.getState()}"));
		Assert.assertEquals("BB", env.parse("{$bObj.state}"));
		Assert.assertEquals("", env.parse("{NONE}"));
		Assert.assertEquals("", env.parse("{$NONE}"));
		
		Assert.assertEquals("abcdefg", env.parse("{normal}"));
		Assert.assertEquals("abcdefg", env.parse("{$normal}"));
		
		//Assert.assertEquals(":", env.parse("{:use:}")); // org.apache.commons.jexl2.JexlException
		Assert.assertEquals(":", env.parse("{$:use:}"));
		
		Assert.assertEquals("@", env.parse("{@use@}"));
		Assert.assertEquals("@", env.parse("{$@use@}"));
		
		//Assert.assertEquals("#", env.parse("{#use#}")); // Function name parse error
		Assert.assertEquals("#", env.parse("{$#use#}"));
		
		//Assert.assertEquals("#", env.parse("{()use()}")); // org.apache.commons.jexl2.JexlException
		Assert.assertEquals("()", env.parse("{$()use()}"));
		
		Assert.assertEquals("$", env.parse("{$$use$}"));
		
		Assert.assertEquals("{", env.parse("{left_brace}"));
		Assert.assertEquals("}", env.parse("{$right_brace}"));
	}
	
	@Test
	public void testFunction_AnnotationProcessing() throws Throwable {
		Assert.assertEquals("a_", env.parse("{#getString(a)}"));
	}
	
	@Test
	public void testFunction_NameSpace() throws Throwable {
		Assert.assertEquals("_", env.parse("{#a:getString()}"));
		Assert.assertEquals("+", env.parse("{#b:getString()}"));
	}
	
	@Test
	public void testFunction_Parameter() throws Throwable {
		Assert.assertEquals("AA_BB_", env.parse("{#getString({aObj.getState()},{bObj.getState()})}"));
		Assert.assertEquals("BB_CC_", env.parse("{#getString({{'bObj'}.getState()},{{'c'}Obj.{'getState()'}})}"));
		
		Object[] parameters = (Object[])env.parse("{#getParameters({','},{' '},',{'('},{')'})}");
		Assert.assertEquals(",", parameters[0]);
		Assert.assertEquals(" ", parameters[1]);
		Assert.assertEquals("'", parameters[2]);
		Assert.assertEquals("(", parameters[3]);
		Assert.assertEquals(")", parameters[4]);
	}
	
	@Test
	public void testFunction_Return() throws Throwable {
		Assert.assertEquals("", env.parse("{#getString2(a)}"));
	}
	
	@Test
	public void testFunction_FindMethod() throws Throwable {
		Assert.assertEquals("AA_", env.parse("{#getString({aObj})}"));
		Assert.assertEquals("BB_", env.parse("{#getString({bObj})}"));
		Assert.assertEquals("CC_", env.parse("{#getString({cObj})}"));
		Assert.assertEquals("a_b_", env.parse("{#getString(a,b)}"));
		
		Assert.assertEquals("", env.parse("{#b:getString(false)}"));
		Assert.assertNotNull(env.parse("{#b:getString(false)}"));
		Assert.assertEquals(" ", env.parse("{#b:getString(1)}"));
	}
	
	@Test
	public void testFunction_FunctionName() {
		Pattern funcNamePattern = Pattern.compile("#[a-zA-Z]([a-zA-Z]|[0-9]|_)*(:[a-zA-Z]([a-zA-Z]|[0-9]|_)*)?");  // 支持Function命名空间
		
		Assert.assertEquals(true, funcNamePattern.matcher("#a").matches());
		Assert.assertEquals(true, funcNamePattern.matcher("#ab").matches());
		Assert.assertEquals(true, funcNamePattern.matcher("#ab2").matches());
		Assert.assertEquals(true, funcNamePattern.matcher("#a_b2_").matches());
		Assert.assertEquals(true, funcNamePattern.matcher("#a:a").matches());
		Assert.assertEquals(true, funcNamePattern.matcher("#ab:ab").matches());
		Assert.assertEquals(true, funcNamePattern.matcher("#ab1:ab2").matches());
		Assert.assertEquals(true, funcNamePattern.matcher("#ab1:a_b2_").matches());
		
		Assert.assertEquals(false, funcNamePattern.matcher("#1a").matches());
		Assert.assertEquals(false, funcNamePattern.matcher("#1").matches());
		Assert.assertEquals(false, funcNamePattern.matcher("#_a").matches());
		Assert.assertEquals(false, funcNamePattern.matcher("#*a").matches());
		Assert.assertEquals(false, funcNamePattern.matcher("#:").matches());
		Assert.assertEquals(false, funcNamePattern.matcher("#").matches());
		Assert.assertEquals(false, funcNamePattern.matcher("").matches());
		Assert.assertEquals(false, funcNamePattern.matcher("#a:").matches());
		Assert.assertEquals(false, funcNamePattern.matcher("#a:1").matches());
		Assert.assertEquals(false, funcNamePattern.matcher("#a:1a").matches());
		Assert.assertEquals(false, funcNamePattern.matcher("#a:_a").matches());
		Assert.assertEquals(false, funcNamePattern.matcher("#a:*a").matches());
		Assert.assertEquals(false, funcNamePattern.matcher("#a:a:a").matches());
	}
}
