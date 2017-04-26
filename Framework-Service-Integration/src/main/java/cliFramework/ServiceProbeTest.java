package cliFramework;

import org.junit.Test;

import static org.junit.Assert.*;

public class ServiceProbeTest {

	ServiceProbe sp = new ServiceProbe();
	

	@Test
	public void testExecuteCommand() {
		System.out.println("ServiceProbeTest.testExecuteCommand");
		String rs = sp.executeCommand(" docker service inspect --format='{{.Spec.Mode.Replicated.Replicas}}' redis ",  "1");
		
		System.out.println("value as substring: " + rs.substring(1,2));
		int intrs = Integer.parseInt(rs.substring(1,2));
		System.out.println("value as int "  +intrs);
		
		
		assertEquals (1 , intrs	);
		//fail("Not yet implemented");

}


	
	}

