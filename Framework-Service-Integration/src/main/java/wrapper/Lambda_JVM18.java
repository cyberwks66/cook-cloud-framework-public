package wrapper;


	//Lambda Expressions (Java 8)

	//If you use the cucumber-java8 module, you can write the Step Definitions using lambdas:

	

import cucumber.api.java8.En;

public class Lambda_JVM18  implements En {}
/*    {
 public void MyStepdefs() {
	*       Given("I have (\\d+) cukes in my belly", (Integer cukes) -> {  
	            System.out.format("Cukes: %n\n", cukes);
	        });
	    }
	
public void MyStepdefs2() {
    Given("I have (\\d+) cukes in my belly", (Integer cukes) -> {  
        System.out.format("Cukes: %n\n", cukes);
    });
}
}


*/



/*
If you are going to use the lambda expressions API to write the Step Definitions, you need:

<dependency>
    <groupId>info.cukes</groupId>
    <artifactId>cucumber-java8</artifactId>
    <version>1.2.5</version>
    <scope>test</scope>
</dependency>
*/