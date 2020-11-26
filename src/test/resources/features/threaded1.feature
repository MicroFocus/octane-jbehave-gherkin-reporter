Feature: threaded 1

    Scenario: run with thread
        Given multiple threads
        When running a thread
        Then the feature should run properly
