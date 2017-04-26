package helloFramework;

import org.junit.Test;

import static org.junit.Assert.*;

public class HTTPHelloClientTest {
	
	HTTPHelloClient hac = new HTTPHelloClient();
	String output = "";  //old
	
	@Test
	public void testHelloClient() {
		// fail("Not yet implemented");
		try {
		output = hac.sendGet();
			
			

	
			
			
			
			
			
			
			
			
			
			
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	
	
	
	
	assertEquals ("Hello!" , output);
	//fail("Not yet implemented");


}
}