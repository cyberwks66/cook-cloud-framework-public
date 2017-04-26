package helloFramework;


import org.junit.Test;

import java.util.concurrent.TimeUnit;

import static org.junit.Assert.*;

public class LaunchFrameworkTest {

	LaunchFramework f = new LaunchFramework();
	

	@Test
	public void testExecuteCommand() throws InterruptedException  {
	f.executeCommand();
	TimeUnit.SECONDS.sleep(60);
	}	
/*	@Test
	public void testDiscoveryCheckpoint() throws InterruptedException {
	f.executeCommand();
	TimeUnit.SECONDS.sleep(60);
	}
	@Test
	public void testRouterCheckpoint() throws InterruptedException {
	f.executeCommand();
	TimeUnit.SECONDS.sleep(60);
	}
	@Test
	public void testElasticsearchCheckpoint() throws InterruptedException {
	f.executeCommand();
	TimeUnit.SECONDS.sleep(60);
	}
	@Test
	public void testKibanaCheckpoint() throws InterruptedException {
	f.executeCommand();	
	TimeUnit.SECONDS.sleep(60);	
		// assertEquals ("ebrh0p411a1zyirq94okam2i1" , rs);
		//fail("Not yet implemented");
	}
		
	
	
	@Test(expected=IndexOutOfBoundsException.class) public void outOfBounds() {
	    //   new ArrayList<Object>().get(1);
	    }
	@Test(timeout=100) public void infinity() {
	       while(true);
	    }
	// public class TestNameTest {
		  @Rule
		  public TestName name= new TestName();

		  @Test
		  public void testA() {
		      assertEquals("testA", name.getMethodName());
		     }

		  @Test
		  public void testB() {
		      assertEquals("testB", name.getMethodName());
		     }
		// }
*/	
}
