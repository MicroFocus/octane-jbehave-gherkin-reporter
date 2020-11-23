@TAG_FEATURE
Feature: name validation

    Scenario: My name is Mike
        Given My name is Mike
        When Someone calls me Mike
        Then I should answer

    Scenario: My name is Jack
        Given My name is Jack
        When Someone calls me Albert
        Then I shouldn't answer