package helloFramework;

import org.junit.Test;

import static org.junit.Assert.*;

public class HelloRouterTest {

	@Test
	public void testNegative() {
		fail("Not yet implemented");
	}
	
	
	@Test
	public void testGetMessage() {
	
	HelloRouter service = new HelloRouter();
	   
	
	String message = service.getMessage();
	
	     
	
	    assertEquals( "Hello Router", message );
	
	  }

	

}
