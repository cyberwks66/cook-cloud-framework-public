package helloFramework;

import org.junit.Test;

import static org.junit.Assert.*;

public class HTTPCookClientTest {
	
	HTTPCookClient hcc = new HTTPCookClient();
	String output = "";  //old
	
	@Test
	public void testCookClient() {
		// fail("Not yet implemented");
		try {
		output = hcc.sendGet();
			
			

	
			
			
			
			
			
			
			
			
			
			
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	
	
	
	
	assertEquals ("Hello!" , output);
	//fail("Not yet implemented");


}
}