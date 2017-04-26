package wrapper;

import cucumber.api.java.en.Given;

//Annotated methods (Java 6 and onwards)

//If you use the cucumber-java module, you can write them using annotated methods:



public class Annotation_JVM17 {
    @Given("I have (\\d+) cukes in my belly")
    public void I_have_cukes_in_my_belly(int cukes) {
        System.out.format("Cukes: %n\n", cukes);
    }
}


/*
Otherwise, to write them using annotated methods, you need:

<dependency>
    <groupId>info.cukes</groupId>
    <artifactId>cucumber-java</artifactId>
    <version>1.2.5</version>
    <scope>test</scope>
</dependency>
*/