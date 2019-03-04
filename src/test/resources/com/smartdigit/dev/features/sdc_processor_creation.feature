@all @sdc @sdc_processor_creation
Feature: SDC workflow

    The feature checks operations:
      1. Create, Edit, Delete rule.
      2. Create, delete tag and rule mapping

  Scenario: [SDC] Create new rule
    Given There is no rule with name "MultiplyOn1001" in SDC
    When Create rule with name "MultiplyOn1001", description "MO100", inputType "LONG", outputType "LONG" and expression "value = value * 100"
    Then Rule "MultiplyOn1001" exists in SDC



  Scenario: [SDC] Edit rule
    Given Rule "MultiplyOn1001" exists in SDC
    When Edit rule with name "MultiplyOn1001".Set description "Edit100", inputType "STRING", outputType "STRING" and expression "value = value * 20"
    Then Rule with name "MultiplyOn1001" contains "description" : "Edit100"
     And Rule with name "MultiplyOn1001" contains "inputType" : "STRING"
     And Rule with name "MultiplyOn1001" contains "outputType" : "STRING"
     And Rule with name "MultiplyOn1001" contains "script" : "value = value * 20"

  Scenario: [SDC] Bind tag to a rule
    Given Rule "MultiplyOn1001" exists in SDC
    When Bind tag "UNH_TEST.R1027.LI447.OL" to rule "MultiplyOn1001"
    Then Tag "UNH_TEST.R1027.LI447.OL" and rule "MultiplyOn1001" are binded "true"

  Scenario: [SDC] Delete binding tag and rule
    Given Tag "UNH_TEST.R1027.LI447.OL" and rule "MultiplyOn1001" are binded "true"
    When Delete binding tag "UNH_TEST.R1027.LI447.OL" and rule "MultiplyOn1001"
    Then Tag "UNH_TEST.R1027.LI447.OL" and rule "MultiplyOn1001" are binded "false"

  Scenario: [SDC] Delete rule
    Given Rule "MultiplyOn1001" exists in SDC
    When Delete rule "MultiplyOn1001" from SDC
    Then There is no rule with name "MultiplyOn1001" in SDC

  Scenario: [SDC] Check SDC EH for notifications
    Given There are 6 notification in SDC EH


#  Scenario: Bind T and R
#    Given Bind tag "TEST.R.R1.C001C.OFGP.Q362TOT.CALC" to rule "MultiplyOn100"
