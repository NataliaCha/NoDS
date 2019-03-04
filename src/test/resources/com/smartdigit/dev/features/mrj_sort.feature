@all @mrj @mrj_sort
Feature: MRJ workflow

    The feature checks operations:
      1. Create test data in database.
      2. Sort by tag, id, pointCount, errorType, createAt, receivedAt, dataSet.
      3. Clear database from test data.


  Scenario Outline: [MRJ] insertTestDate
    Given Insert asset with "<name>", "<eType>", "<pCount>"
  Examples:
    |name                   |eType|pCount|
    |ABTEST.R1026.LI555.OL  |999    |0|
    |AATEST.R1026.LI555.OL  |999    |1|
    |BBTEST.R1026.LI555.OL  |999    |3|
    |BATEST.R1026.LI555.OL  |999    |4|
    |ERTEST.R1026.LI555.OL  |6      |4|
    |ERTEST.R1026.LI555.OL  |4      |4|
    |ERTEST.R1026.LI555.OL  |3      |4|
    |ERTEST.R1026.LI555.OL  |2      |4|

  Scenario: [MRJ] Sorting by tag
   When Sorting by name "tag" and etype "999" with param "asc" and result is
    |AATEST.R1026.LI555.OL  |
    |ABTEST.R1026.LI555.OL  |
    |BATEST.R1026.LI555.OL  |
    |BBTEST.R1026.LI555.OL  |
   And Sorting by name "tag" and etype "999" with param "desc" and result is
    |BBTEST.R1026.LI555.OL  |
    |BATEST.R1026.LI555.OL  |
    |ABTEST.R1026.LI555.OL  |
    |AATEST.R1026.LI555.OL  |

  Scenario: [MRJ] Sorting by pointCount
   When Sorting by name "pointCount" and etype "999" with param "asc" and result is
    |0|
    |1|
    |3|
    |4|
   And Sorting by name "pointCount" and etype "999" with param "desc" and result is
    |4|
    |3|
    |1|
    |0|

  Scenario: [MRJ] Sorting by Time
   When Sorting with name "createdTs" and etype "999" by param "asc" is result
    |ABTEST.R1026.LI555.OL  |
    |AATEST.R1026.LI555.OL  |
    |BBTEST.R1026.LI555.OL  |
    |BATEST.R1026.LI555.OL  |
   And Sorting with name "createdTs" and etype "999" by param "desc" is result
    |BATEST.R1026.LI555.OL  |
    |BBTEST.R1026.LI555.OL  |
    |AATEST.R1026.LI555.OL  |
    |ABTEST.R1026.LI555.OL  |

  Scenario: [MRJ] sorting_by_Time
    When Sorting with name "receivedTs" and etype "999" by param "asc" is result
    |ABTEST.R1026.LI555.OL  |
    |AATEST.R1026.LI555.OL  |
    |BBTEST.R1026.LI555.OL  |
    |BATEST.R1026.LI555.OL  |

  Scenario: [MRJ] sorting_by_Time
    When Sorting with name "receivedTs" and etype "999" by param "desc" is result
    |BATEST.R1026.LI555.OL  |
    |BBTEST.R1026.LI555.OL  |
    |AATEST.R1026.LI555.OL  |
    |ABTEST.R1026.LI555.OL  |

  Scenario: [MRJ] sorting_by_Id
    When Sorting with name "id" and etype "999" by param "asc" is result
    |ABTEST.R1026.LI555.OL  |
    |AATEST.R1026.LI555.OL  |
    |BBTEST.R1026.LI555.OL  |
    |BATEST.R1026.LI555.OL  |

  Scenario: [MRJ] sorting_by_Id
    When Sorting with name "id" and etype "999" by param "desc" is result
    |BATEST.R1026.LI555.OL  |
    |BBTEST.R1026.LI555.OL  |
    |AATEST.R1026.LI555.OL  |
    |ABTEST.R1026.LI555.OL  |

  Scenario: [MRJ] sorting_by_etype
    When Sorting tag "ERTEST.R1026.LI555.OL" by etype with name "errorType" and param "asc" is result
    |2|
    |3|
    |4|
    |6|

  Scenario: [MRJ] sorting_by_etype
    When Sorting tag "ERTEST.R1026.LI555.OL" by etype with name "errorType" and param "desc" is result
    |6|
    |4|
    |3|
    |2|

  Scenario: [MRJ] sorting_by_tag_with_PageNumber
    When Sorting with filtred pageNumber "1" pageSize "2" with name "tag" etype "999" by param "asc" is result
    |BATEST.R1026.LI555.OL|
    |BBTEST.R1026.LI555.OL|

  Scenario Outline: [MRJ] deleteTestDates
        Then Delete msg with "<name>"
      Examples:
    |name                 |
    |AATEST.R1026.LI555.OL|
    |ABTEST.R1026.LI555.OL|
    |BBTEST.R1026.LI555.OL|
    |BATEST.R1026.LI555.OL|
    |ERTEST.R1026.LI555.OL|
