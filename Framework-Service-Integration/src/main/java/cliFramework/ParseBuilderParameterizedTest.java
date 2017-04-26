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
public class ParseBuilderParameterizedTest {
    @Parameters (name = "{index}: Service({1})={2}")
    public static Collection<Object[]> data() {
        return Arrays.asList(new Object[][] {     
   //              { "docker node inspect moby ","Reachability", "reachable" }
   //             ,{ "docker node inspect moby ", "CreatedAt", "2016-11-01T07:52:28.9131808Z"} 
   //             ,{ "docker node inspect moby ", "Role", "manager"} 
   //             ,{ "docker node inspect moby ", "EngineVersion", "1.12.1"} 
   //             ,{ "docker node inspect moby ", "Addr", "10.0.75.2:2377"} 
                 { "docker service inspect redis ", "Replicas", "1"} 
                 ,{ "docker service inspect --format='{{.Spec.Mode.Replicated.Replicas}}' redis", "1", "1"}, 
                { "docker node inspect moby ", "EngineVersion", "1.12.1"} 
                 ,{ "docker node inspect moby ", "Addr", "10.0.75.2:2377"} 
                 ,{ "docker node inspect moby ", "ID", "5hk6jl0ak3i0xy2r8m4x0wm3z"} 
                 ,{ "docker node inspect moby ", "CreatedAt", "2016-11-01T07:52:28.9131808Z"} 
                 ,{ "docker node inspect moby ", "Role", "manager"} 
                 ,{ "docker node inspect moby ", "EngineVersion", "1.12.1"} 
                 ,{ "docker node inspect moby ", "Addr", "10.0.75.2:2377"} 
                 ,{ "docker node inspect moby ", "ID", "5hk6jl0ak3i0xy2r8m4x0wm3z"} 
                
                 
                 
                 
                 
                  
           });
    }

    ParseBuilder pb = new ParseBuilder();
 //   String rs = pb.executeCommand("docker node inspect moby ","Reachability", "reachable" );

    private String tActual;
    private String tExpected;
	
	
    
    
    
    public ParseBuilderParameterizedTest(String command, String tag, String expected) {
    	System.out.println("ParseBuilderParameterizedTest.executeCommand");
    	 tActual = pb.executeCommand(command, tag, expected );
    	
   // 	tActual= rs;
        tExpected= expected;
        
        
        System.out.println("tExpected:  " + tExpected);
        System.out.println("tActual:  " + tActual);
        
    }

    @Test
    public void test() {
    	  assertEquals(tExpected, tActual);
    }
}
