package helloFramework;

import org.junit.Test;

import static org.junit.Assert.*;

public class HTTPKibanaClientTest {
	
	HTTPKibanaClient hac = new HTTPKibanaClient();
	String output = "";  //old
	
	@Test
	public void testKibanaClient() {
		// fail("Not yet implemented");
		try {
		output = hac.sendGet();
			
			

	
			
			
			
			
			
			
			
			
			
			
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	
	
	
	
	assertEquals ("200" , output);
	//fail("Not yet implemented");


}
}