Feature: failed on background
    Background:
        Given number 6
        When step fails
        Then this should be skipped

        Scenario: skipped
            When reducing 4
            Then we should get 2