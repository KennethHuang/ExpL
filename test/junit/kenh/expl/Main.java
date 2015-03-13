package junit.kenh.expl;

import org.junit.runner.Result;
import org.junit.runner.notification.Failure;

import java.util.List;

public class Main {
	
	public static void main(String[] args) {
		
		Class[] classes = new Class[] { 
			EnvironmentTest.class,
			ParserTest.class,
			ExceptionTest.class
		};
		
		Result result = org.junit.runner.JUnitCore.runClasses(classes);
		
		int runCount = result.getRunCount();
		int ignoreCount = result.getIgnoreCount();
		int failureCount = result.getFailureCount();
		System.out.println("Run: " + runCount + "/" + (runCount + ignoreCount));
		System.out.println("Failure: " + failureCount);
		System.out.println();
		
		if(failureCount > 0) {
			List<Failure> failures = result.getFailures();
			for(Failure failure: failures) {
				System.out.println(failure.toString());
			}
		}
	}

}
