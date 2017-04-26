Feature: Monday_User_Story

  Background:
    Given There is a person

  Scenario: It is Monday morning
    And it is < 8:00 a.m.
    When swarm nodecount is run
    Then nodes should equal 3
    
  Scenario: It is Monday morning
    And it is > 8:00 a.m.
    When swarm nodecount is run
    Then nodes should equal 3 add 2 (cloud and swarm) 
    
  Scenario: It is Monday morning
    And it is < 8:15 a.m.
    When service inspect is run
    And service is hello-world
    Then replicas should equal 1 
    
  Scenario: It is Monday morning
    And it is > 8:15 a.m.
    When service inspect is run
    And service is hello-world
    Then replicas should equal 1 add 2   
    
   
 Scenario: It is Monday morning
    And it is < 8:30 a.m.
    When service inspect is run
    And service is hello-world-1.0.0
    Then traffic should equal 1.00 
 
 Scenario: It is Monday morning
    And it is < 8:30 a.m.
    When service router is run
    And service is hello-world-1.0.1
    Then traffic should equal .00
 
 Scenario: It is Monday morning
    And it is > 8:30 a.m.
    When service inspect is run
    And service is hello-world-1.0.0
    Then traffic should equal .99
 
 Scenario: It is Monday morning
    And it is > 8:30 a.m.
    When RabbitMQ log is run
    And service is hello-world-1.0.1
    Then ExceptionCountAlert should be true
    
 Scenario: It is Monday morning
    And it is > 8:45 a.m.
    When service inspect is run
    And service is hello-world-1.0.0
    Then traffic should equal 1.00 

  Scenario: It is Monday morning
    And it is > 8:45 a.m.
    When service inspect is run
    And service is hello-world-1.0.0
    Then traffic should equal 1.00 
 
 Scenario: It is Monday morning
    And it is > 8:45 a.m.
    When service router is run
    And service is hello-world-1.0.1
    Then traffic should equal .00  
    
    
    
     
      
 