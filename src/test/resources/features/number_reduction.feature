@TIDrevBlaBla
Feature: reduction

    Scenario: 6 minus 4
        Given number 6
        When reducing 4
        Then we should get 2

    Scenario: 3 minus 2
        Given number 3
        When reducing 2
        Then we should get 1