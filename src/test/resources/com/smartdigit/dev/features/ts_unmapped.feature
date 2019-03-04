@all @unmapped
Feature: Message reaches

  Assumptions for testing purpose:
  1. Unique sensor name for tag attribute (with "test" index).
  2. On checking we take the latest pack on data points from REST response.
  3. On checking messageId is used to find Data point.


  Scenario Outline: [Sys] ts-unmapped
    When Publish message with "<tag>", "<unit>" and "<value>"
    And Wait 30 sec
    Then Message with such "<tag>", "<unit>" and "<value>" exists in Postgres
    Examples:
      |tag                              |unit | value |
      |UNH_TT.TEST.LI445.F              |bar  |123.12 |
      |UNH_TT.TEST.LI446.F              |bar  |222.22 |