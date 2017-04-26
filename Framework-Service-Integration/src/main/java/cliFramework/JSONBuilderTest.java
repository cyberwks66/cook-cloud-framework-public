package cliFramework;

import org.junit.Test;

import static org.junit.Assert.*;

public class JSONBuilderTest {

	JSONBuilder jb = new JSONBuilder();
	

	@Test
	public void testExecuteCommand() {
		System.out.println("xxx");
		String rs = jb.executeCommand("docker node inspect moby ");
	//	String rs = jb.executeCommand("docker node inspect moby --format \"{{ Status.State}}\"");
		
		assertEquals ("reachable" , rs);
		//fail("Not yet implemented");

}
}
