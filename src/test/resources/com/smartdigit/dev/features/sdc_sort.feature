@all @sdc @sdc_sort
Feature: SDC sort

    The feature checks operations:
      1. Create test data in database.
      2. Sort by name, id, description, inputType, outputType, templateId, script, createdAt, updatedAt, createdBy, updatedBy.
      3. Clear database from test data.


  Scenario: [SDC] Create new test_rule
  Given Create test_sorting rule with name "SDC_SORTING2", description "HERE", inputType "BOOLEAN", outputType "LONG", templateId "1", script "value = value * 2", createdBy "Tester_QA", updatedBy "Tester_updater4"
  Given Create test_sorting rule with name "SDC_SORTING1", description "NOT_HERE", inputType "DOUBLE", outputType "LONG", templateId "1", script "value = value * 3", createdBy "Tester_QA", updatedBy "Tester_updater2"
  Given Create test_sorting rule with name "SDC_SORTING3", description "SOMEWHERE", inputType "STRING", outputType "STRING", templateId "1", script "value = value * 4", createdBy "Tester_QA", updatedBy "Tester_updater1"
  Given Create test_sorting rule with name "SDC_SORTING5", description "HERE2", inputType "LONG", outputType "BOOLEAN", templateId "1", script "value = value * 1", createdBy "Tester_QA", updatedBy "Tester_updater3"
  Given Create test_sorting rule with name "SDC_SORTING6", description "NOT_HERE", inputType "STRING", outputType "LONG", templateId "1", script "value = value * 6", createdBy "Boris", updatedBy "Tester_updater5"
  Given Create test_sorting rule with name "SDC_SORTING7", description "HERE", inputType "STRING", outputType "LONG", templateId "1", script "value = value * 7", createdBy "Vlad", updatedBy "Tester_updater5"
  Given Create test_sorting rule with name "SDC_SORTING8", description "HERE", inputType "STRING", outputType "LONG", templateId "1", script "value = value * 8", createdBy "Andrey", updatedBy "Tester_updater5"
  Given Create test_sorting rule with name "SDC_SORTING9", description "HERE", inputType "STRING", outputType "LONG", templateId "1", script "value = value * 9", createdBy "David", updatedBy "Tester_updater5"
#


#    Given Create test_sorting rule with "<name>", "<description>", "<inputType>", "<outputType>", "<templateId>", "<expression>", "<createdBy>"
#  Examples:
#    |name          |description|inputType|outputType|templateId|script       |createdBy|
#    |SDC_SORTING2  |HERE       |STRING   |LONG      |1       |value = value * 2|Boris    |
#    |SDC_SORTING1  |NOT_HERE   |LONG     |LONG      |1       |value = value * 3|Vlad     |
#    |SDC_SORTING3  |SOMEWHERE  |STRING   |STRING    |1       |value = value * 4|Andrey   |
#    |SDC_SORTING5  |HERE2      |STRING   |LONG      |1       |value = value * 1|Denis    |
#    |SDC_SORTING4  |NOT_HERE   |LONG     |STRING    |1       |value = value * 6|Pavel    |
#    |SDC_SORTING4  |SOMEWHERE  |LONG     |STRING    |1       |value = value * 7|Max      |
#    |SDC_SORTING4  |HERE3      |STRING   |STRING    |1       |value = value * 8|Gustav   |
#    |SDC_SORTING4  |NOT_HERE   |STRING   |LONG      |1       |value = value * 9|Sergey   |


  Scenario: [SDC] Sorting rule by name
    When Sorting rule by name "name" and createdBy "Tester_QA" with param "asc" and result is
    |SDC_SORTING1|
    |SDC_SORTING2|
    |SDC_SORTING3|
    |SDC_SORTING5|
    And Sorting rule by name "name" and createdBy "Tester_QA" with param "desc" and result is
    |SDC_SORTING5|
    |SDC_SORTING3|
    |SDC_SORTING2|
    |SDC_SORTING1|

  Scenario: [SDC] Sorting rule by description
    When Sorting rule by name "description" and createdBy "Tester_QA" with param "asc" and result is
    |HERE|
    |HERE2|
    |NOT_HERE|
    |SOMEWHERE|
    And Sorting rule by name "description" and createdBy "Tester_QA" with param "desc" and result is
    |SOMEWHERE|
    |NOT_HERE|
    |HERE2|
    |HERE|

  Scenario: [SDC] Sorting rule by inputType
    When Sorting rule by name "inputType" and createdBy "Tester_QA" with param "asc" and result is
    |BOOLEAN|
    |DOUBLE|
    |LONG|
    |STRING|
    And Sorting rule by name "inputType" and createdBy "Tester_QA" with param "desc" and result is
    |STRING|
    |LONG|
    |DOUBLE|
    |BOOLEAN|

  Scenario: [SDC] Sorting rule by outputType
    When Sorting rule by name "outputType" and createdBy "Tester_QA" with param "asc" and result is
    |BOOLEAN|
    |LONG|
    |LONG|
    |STRING|
    And Sorting rule by name "outputType" and createdBy "Tester_QA" with param "desc" and result is
    |STRING|
    |LONG|
    |LONG|
    |BOOLEAN|

  Scenario: [SDC] Sorting rule by script
    When Sorting rule by name "script" and createdBy "Tester_QA" with param "asc" and result is
    |value = value * 1|
    |value = value * 2|
    |value = value * 3|
    |value = value * 4|
    And Sorting rule by name "script" and createdBy "Tester_QA" with param "desc" and result is
    |value = value * 4|
    |value = value * 3|
    |value = value * 2|
    |value = value * 1|

  Scenario: [SDC] Sorting rule by updatedBy
    Given Update test_sorting rule with name "SDC_SORTING2", description "HERE", inputType "BOOLEAN", outputType "LONG", templateId "1", script "value = value * 2", updatedBy "Tester_updater4"
    Given Update test_sorting rule with name "SDC_SORTING1", description "NOT_HERE", inputType "DOUBLE", outputType "LONG", templateId "1", script "value = value * 3", updatedBy "Tester_updater2"
    Given Update test_sorting rule with name "SDC_SORTING3", description "SOMEWHERE", inputType "STRING", outputType "STRING", templateId "1", script "value = value * 4", updatedBy "Tester_updater1"
    Given Update test_sorting rule with name "SDC_SORTING5", description "HERE2", inputType "LONG", outputType "BOOLEAN", templateId "1", script "value = value * 1", updatedBy "Tester_updater3"

    When Sorting rule by name "updatedBy" and createdBy "Tester_QA" with param "asc" and result is
    |Tester_updater1|
    |Tester_updater2|
    |Tester_updater3|
    |Tester_updater4|
    And Sorting rule by name "updatedBy" and createdBy "Tester_QA" with param "desc" and result is
    |Tester_updater4|
    |Tester_updater3|
    |Tester_updater2|
    |Tester_updater1|

  Scenario: [SDC] Sorting rule by templateId
    Given Create test_sorting templateId with name "SDC_TEMPLATE2" and createdBy "Template_Creator"
    Given Create test_sorting templateId with name "SDC_TEMPLATE1" and createdBy "Template_Creator"
    Given Create test_sorting templateId with name "SDC_TEMPLATE3" and createdBy "Template_Creator"
    When Sorting rule by name "templateId" and createdBy "Template_Creator" with param "asc" and result tags list is
    |SDC_TEMPLATE2|
    |SDC_TEMPLATE1|
    |SDC_TEMPLATE3|
    And Sorting rule by name "templateId" and createdBy "Template_Creator" with param "desc" and result tags list is
    |SDC_TEMPLATE3|
    |SDC_TEMPLATE1|
    |SDC_TEMPLATE2|

  Scenario: [SDC] Sorting rule by createdBy
    Given Update test_sorting rule with name "SDC_SORTING6", description "NOT_HERE", inputType "STRING", outputType "LONG", templateId "1", script "value = value * 6", updatedBy "Tester_updater5"
    Given Update test_sorting rule with name "SDC_SORTING7", description "HERE", inputType "STRING", outputType "LONG", templateId "1", script "value = value * 7", updatedBy "Tester_updater5"
    Given Update test_sorting rule with name "SDC_SORTING8", description "HERE", inputType "STRING", outputType "LONG", templateId "1", script "value = value * 8", updatedBy "Tester_updater5"
    Given Update test_sorting rule with name "SDC_SORTING9", description "HERE", inputType "STRING", outputType "LONG", templateId "1", script "value = value * 9", updatedBy "Tester_updater5"
    When Sorting rule by name "createdBy" and updatedBy "Tester_updater5" with param "asc" and result is
    |Andrey|
    |Boris|
    |David|
    |Vlad|
    And Sorting rule by name "createdBy" and updatedBy "Tester_updater5" with param "desc" and result is
    |Vlad|
    |David|
    |Boris|
    |Andrey|

  Scenario: [SDC] Sorting rule by createdAt
    When Sorting rule by name "createdAt" and createdBy "Tester_QA" with param "asc" and result tags list is
    |SDC_SORTING2  |
    |SDC_SORTING1  |
    |SDC_SORTING3  |
    |SDC_SORTING5  |
    And Sorting rule by name "createdAt" and createdBy "Tester_QA" with param "desc" and result tags list is
    |SDC_SORTING5  |
    |SDC_SORTING3  |
    |SDC_SORTING1  |
    |SDC_SORTING2  |

  Scenario: [SDC] Sorting rule by updatedAt
    When Sorting rule by name "updatedAt" and createdBy "Tester_QA" with param "asc" and result tags list is
    |SDC_SORTING2  |
    |SDC_SORTING1  |
    |SDC_SORTING3  |
    |SDC_SORTING5  |
    And Sorting rule by name "updatedAt" and createdBy "Tester_QA" with param "desc" and result tags list is
    |SDC_SORTING5  |
    |SDC_SORTING3  |
    |SDC_SORTING1  |
    |SDC_SORTING2  |

  Scenario: [SDC] Sorting rule by id
    When Sorting rule by name "id" and createdBy "Tester_QA" with param "asc" and result tags list is
    |SDC_SORTING2|
    |SDC_SORTING1|
    |SDC_SORTING3|
    |SDC_SORTING5|
    And Sorting rule by name "id" and createdBy "Tester_QA" with param "desc" and result tags list is
    |SDC_SORTING5|
    |SDC_SORTING3|
    |SDC_SORTING1|
    |SDC_SORTING2|

  Scenario: [SDC] Delete test rule and templateId
    When Delete test_templateId with name "SDC_TEMPLATE2" from SDC
    When Delete test_templateId with name "SDC_TEMPLATE1" from SDC
    When Delete test_templateId with name "SDC_TEMPLATE3" from SDC
    When Delete rule "SDC_SORTING2" from SDC
    When Delete rule "SDC_SORTING1" from SDC
    When Delete rule "SDC_SORTING3" from SDC
    When Delete rule "SDC_SORTING5" from SDC
    When Delete rule "SDC_SORTING6" from SDC
    When Delete rule "SDC_SORTING7" from SDC
    When Delete rule "SDC_SORTING8" from SDC
    When Delete rule "SDC_SORTING9" from SDC
#    When Delete rule "SDC_TEMPLATE2" from SDC
#    When Delete rule "SDC_TEMPLATE1" from SDC
#    When Delete rule "SDC_TEMPLATE3" from SDC
