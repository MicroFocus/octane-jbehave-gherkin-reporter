@TAG_FEATURE
Feature: addition

    Background:
        Given i know math
        Then i can calculate

    Scenario: add 111 and 222
        Given a number with a value of 111
        When we add 222 to the number
        Then the number value should be 333


    Scenario Outline: add <num1> and <num2>
        Given a number with a value of <num1>
        When we add <num2> to the number
        Then the number value should be <result>

        Examples:
            | num1 | num2 | result |
            | 500  | 600  | 1100   |
            | 4    | 5    | 9      |