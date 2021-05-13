# ExpL

The source files are under [xScript](https://github.com/KennethHuang/xScript).

**ExpL (Expression Language)**, is a tool to parse string expression. It base on [Apache commons JEXL](http://commons.apache.org/proper/commons-jexl/) & [Apache Commons BeanUtils](http://commons.apache.org/proper/commons-beanutils/). It support nesting, function, etc.

Example:

	kenh.expl.Environment env = new kenh.expl.Environment(); 
	env.setVariable("item1", "ABCDEFG");
	env.setVariable("object1", new Object());
	System.out.println(env.parse("{item1}"));  // ABCDEFG
	System.out.println(env.parse("{object1}"));    // java.lang.Object@1c436b
	
	// Return object array, when second parameter is true.
	// [Ljava.lang.Object;@d8dc5a
	System.out.println(env.parse("{item1},{object1}",true));    


***

## 1. Introduce

### Nesting

	System.out.println(env.parse("{{1+1}+1}"));  // 3 
	System.out.println(env.parse("{item{1+0}}"));  // ABCDEFG 

### Function

To use function, you should use #, the format is _#functionName(parameter1...parameterN)_ or _#functionName()_

	System.out.println(env.parse("{#startsWith({item1},ABC)}")); 

Function can have namespace, please see the section below.

### Other

env.parse(String) return an object, it's a String or a non-String object. but if you put them together, the exception will be thrown.

	System.out.println(env.parse("string here, {object1}"));  // Exception is thrown.  


***

## 2. JEXL or BeanUtils

ExpL use JEXL and BeanUtils to parse expression. To use BeanUtils, you should put $ in front of expression.

	System.out.println(env.parse("{item1.getClass().getName()}"));  // use JEXL  
	System.out.println(env.parse("{$item1.class.name}"));  // use BeanUtils  

_item1.getClass().getName()_ use JEXL to parse, item1.class.name use BeanUtils.

Please visit homepage of JEXL & BeanUtils to learn how to write an expression.


***

## 3. Define a function

It's easy to define you own function.

### Extends _kenh.expl.impl.BaseFunction_

	package your.packagename;  
	public class GetString extends kenh.expl.impl.BaseFunction { 	
		public String process() {   // {#getString()} will call this method  
			return "ABCD";  
		} 
		public String process(String s, String b) {  // {#getString(a,b)} will call this method 
			return s + b;  
		}  
	}  

If you extend _BaseFunction_, the processing method can use name _process_ directly. _BaseFunction_ will call the correct method according to parameter number and parameters' class.

If you do not use _process_ as method name, use annotation _kenh.expl.Processing_.

	@kenh.expl.Processing  
	public String yourMethodName(String a, String b, String c) {   // for {#getString(a,b,c)}
		return "EFG";  
	}  

### Implements _kenh.expl.Function_

	package your.packagename;  
	public class GetString extends kenh.expl.Function {  
		private Environment env = null;  
  	
		@Override  
		public void setEnvironment(Environment env) {  
			this.env = env;  
		}  
	
		@Override  
		public Environment getEnvironment() {  
			return env;  
		}  
  		
		@Override  
		public Object invoke(Object... params) throws UnsupportedExpressionException {  
			return "ABCD";  
		}  
	}  

### Other

The processing method can use _void_ as key word, or return null.

	@kenh.expl.Processing  
	public void noneReturn() { } // this method will return "".  
	
	@kenh.expl.Processing`  
	public Object nullReturn() { // this method will return "".  
		return null;  
	}   

***

## 4. Add your own Function to ExpL

1) _kenh.expl.Environment_ has method _setFunctionPackage(String key, String funcPackage)_ and _setFunctionPackage(String key, String funcPackage)_, the key is the namespace of function.

2) Use system properties to load function.The system property start with kenh.expl.function.packages will be found.

	-kenh.expl.function.packages.functionNameSpace=your.function.packagename  

3) Use _kenh.expl.Extension_ interface. (It use class _java.util.ServiceLoader_ to load. Please see _java.util.ServiceLoader_ for more detail)

	public interface Extension {  
		// return all of you function package  
		public java.util.Map<String, String> getFunctionPackages();  
	}  

***

## 5. The namespace of function

If two function packages have function with same name, you can use namespace to specify the correct function you need.

	env.setFunctionPackage("a", "your.function.packagename.aaa");  
	env.setFunctionPackage("b", "your.function.packagename.bbb");  
	env.parse("{#a:yourMethod()}");  // Call yourMethod in package 'your.function.packagename.aaa'  
	env.parse("{#b:yourMethod()}");  // Call yourMethod in package 'your.function.packagename.bbb'  

***

## 6. Define a parser

You can define your own parser, ex. XPathParser, to support XPath instead of JEXL. You can implement _kenh.expl.Parser_ or extend _kenh.expl.impl.BaseParser_.
