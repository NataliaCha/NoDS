@all @mapped_data_pg
Feature: Checking mapped-data-service

  Note: For scenarios Mapped-data DB populated td-jv-qa-backend have to be deployed.

  Scenario: [MDS] mapped-data-service endpoint
    When Create mapping og "РНПК" and assets
      |Root.PDH.112 Oleflex.112LI003A|
      |UNH_TD.TEST.LI447.OL|
      |TEST.R.R1.C001C.OFGP.Q362TOT.CALC|
      |TEST.R.R1.C001C.OFGP.Q361TOT.CALC|
      |TEST.R.R1.C001C.O47020101.QDAY.PLAN|
      |TEST.R.S1.C0010.K12.F8007.PV|
      |TEST.R.S1.C0010.HT3.Q3006C.CALC|
      |TEST.R.S1.B020.IFG.Q00461C.CALC|
      |TEST.R.S1.V0010.INFS.F09751.PV|
      |TEST.R.S1.V0010.INFS.F09752.PV|
      |TEST.R.S1.C0010.INFS.F0976.PV|
      |TEST.R.S1.C0010.INFS.F09762.PV|
      |TEST.R.S1.C0010.T58.F8502_2.PV|
      |TEST.R.S1.C0010.T58.F8502_1.PV|
      |TEST.R.S1.C0010.ER1.F00381.PV|
    And Wait 2 sec
    Then Asset "Root.PDH.112 Oleflex.112LI003A" mapped with og "РНПК"
    And Asset "UNH_TD.TEST.LI447.OL" mapped with og "РНПК"

  Scenario: [MDS] Mapped-data DB populated
    Given Delete all tags "UNH_TD.TEST.LI447.OL" and quality "1" from DB
    When Publish message with "UNH_TD.TEST.LI447.OL", "1" and "27.27"
    And Wait 10 sec
    Then Tag "UNH_TD.TEST.LI447.OL" with quality "1" and value "2727.00" is in DB
