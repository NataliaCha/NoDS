@all @mapper
Feature: Mapper

  The scenario checks directly mapper. Sending message to input EH and getting from output EH.

  Scenario: [Mapper] Mapped
    Given Tag "UNH_TD.TEST.Map991.WW" and rule "MultiplyOn100" are binded "true"
#    And Asset "UNH_TD.TEST.LI447.OL" mapped with og "РНПК"
    When Publish message with "UNH_TD.TEST.Map991.WW", "3" and "14"
    And Wait 2 sec
    Then Message with such "UNH_TD.TEST.Map991.WW", "3" and "1400" is in EH mapped

  Scenario: [Mapper] Unmapped
    When Publish message with "UNH_TD.TEST.TT001.OL", "4" and "19"
    And Wait 2 sec
    Then Message with such "UNH_TD.TEST.TT001.OL", "4" and "19" is in EH unmapped
