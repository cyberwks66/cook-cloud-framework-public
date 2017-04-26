package cliFramework;

import org.junit.Test;

import static org.junit.Assert.*;

public class ParseBuilderTest {

	ParseBuilder jb = new ParseBuilder();
	

	@Test
	public void testExecuteCommand() {
		System.out.println("ParseBuilderTest.testExecuteCommand");
	//	String rs = jb.executeCommand("docker node inspect moby ", "Architecture", "x86_64");
	//	String rs = jb.executeCommand("docker node inspect moby --format \"{{ Status.State}}\"");
		String rs = jb.executeCommand("docker service inspect redis ", "ID", "ebrh0p411a1zyirq94okam2i");
		
		
		
		assertEquals ("ebrh0p411a1zyirq94okam2i1" , rs);
		//fail("Not yet implemented");

}
}
