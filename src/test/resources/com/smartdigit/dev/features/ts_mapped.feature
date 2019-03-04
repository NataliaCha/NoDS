@all @mapped
Feature: Checking the message reaches TS

  Assumptions for testing purpose:
  1. Search message with expected tag, qual and expVal for last 1 min.
  2. Tags have to be mapped on predefined rule (val*100) in SDC

  Description:
  1. Checking before test that tag and rule are binded.
  2. Checking before test that asset is mapped with og.

  Scenario Outline: [Sys] ts-mapped
    Given Tag "<tag>" and rule "MultiplyOn100" are binded "true"
#    And Asset "<tag>" mapped with og "РНПК"
    When Publish message with "<tag>", "<qual>" and "<sendVal>"
    And Wait 30 sec
    Then Message with such "<tag>", "<qual>" and "<expVal>" exists in TS
    Examples:
      |tag                              |qual | sendVal |   expVal|
      |UNH_TD.TEST.Map991.WW            |7    |1.22     |      122|
      |Root.PDH.112 Oleflex.112LI003A   |8    |39.72    |     3972|

