package cliFramework;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import java.util.Arrays;
import java.util.Collection;

import static org.junit.Assert.*;
import static org.junit.Assert.assertEquals;

@RunWith(Parameterized.class)
public class ServiceProbeParameterizedTest {
    @Parameters (name = "{index}: Instances of Framework Service({1})={2}")
    public static Collection<Object[]> data() {
        return Arrays.asList(new Object[][] {     
  
                 { "docker service inspect --format='{{.Spec.Mode.Replicated.Replicas}}' redis",  "1"} 
                 ,{ "docker service inspect --format='{{.Spec.Mode.Replicated.Replicas}}' redis2",  "1"} 
                 ,{ "docker service inspect --format='{{.Spec.Mode.Replicated.Replicas}}' router",  "1"} 
                 ,{ "docker service inspect --format='{{.Spec.Mode.Replicated.Replicas}}' hello-world",  "1"} 
                 ,{ "docker service inspect --format='{{.Spec.Mode.Replicated.Replicas}}' vertical-scaler",  "1"} 
                 ,{ "docker service inspect --format='{{.Spec.Mode.Replicated.Replicas}}' horizontal-scaler",  "1"} 
                 ,{ "docker service inspect --format='{{.Spec.Mode.Replicated.Replicas}}' elasticsearch",  "1"} 
                 ,{ "docker service inspect --format='{{.Spec.Mode.Replicated.Replicas}}' kibana",  "1"} 
                 ,{ "docker service inspect --format='{{.Spec.Mode.Replicated.Replicas}}' node-manager",  "1"} 
                 ,{ "docker service inspect --format='{{.Spec.Mode.Replicated.Replicas}}' configserver",  "1"} 
                                        
                 
                 
                 
                 
                  
           });
    }

    ServiceProbe sp = new ServiceProbe();
    
    String tActual;
    private String tExpected;
	private int intrs;
//	String s;
    
    
    
    public ServiceProbeParameterizedTest(String command,  String expected) {
    	System.out.println("ParseBuilderParameterizedTest.executeCommand");
    	String rs = sp.executeCommand(command,  expected );
    	
   // 	tActual= rs;
        tExpected = expected;
       
        System.out.println("value as substring: " + rs.substring(1,2));
		intrs = Integer.parseInt(rs.substring(1,2));
		System.out.println("value as int "  +intrs);
		
        
        System.out.println("tExpected:  " + tExpected);
        System.out.println("intActual:  " + intrs);
        
    //    System.out.println(pb.toString().substring(1,2));
    //    s = jb.toString();
    //    System.out.println("s:  " + s);
        
	//	ints = Integer.parseInt(s.substring(1,2));
	//	 System.out.println("s:  " + s);
        
        
    }

    @Test
    public void test() {
    	  assertEquals(1, intrs);
    }
}
