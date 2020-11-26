Feature: failed step on scenario outline

    Scenario Outline: add <num1> and <num2>
        Given a number with a value of <num1>
        When we add <num2> to the number
        Then the number value should be <result>

        Examples:
            | num1 | num2 | result |
            | 500  | 600  | 1100   |
            | 4    | 5    | 10     |