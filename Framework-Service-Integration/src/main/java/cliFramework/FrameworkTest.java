package cliFramework;

import org.junit.Test;

import static org.junit.Assert.*;

public class FrameworkTest {
	Framework fmk = new Framework();

	@Test
	public void testExecuteCommand() {
		String rs = fmk.executeCommand("docker node inspect moby ");
		assertEquals ("reachable" , rs);
		//fail("Not yet implemented");
	}

}
