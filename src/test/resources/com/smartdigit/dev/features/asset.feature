@all @asset
Feature: Assets workflow

  Brief description of Asset functionality
  1. App Asset gets data from POA (Asset service in Predix)
  2. Update and Delete req works based on asset id
  3. Id - unique property for each asset
  4. In scope of bulk load app compares assets by id
    If asset with such id doesn't exist then Create new asset.
    If asset with such id exists, it compare other asset's properties.
      If there are no difference, then skip asset.
      If difference exists, then update asset.
    If asset exist in app but stay out of POA then Delete asset.

  Note: For scenarios Assets full loading and Assets bulk loading td-jv-qa-backend have to be deployed.

#  Scenario: [Asset] Create new asset
#    Given Asset with id "ELOUABT6.P-1-5" exists is false
#    When Create asset with
#      | uri     | /asset/ELOUABT6.P-1-5 |
#      | classif | equipment             |
#      | hide    | true                  |
#      | name    | ELOUABT6.P-1-5        |
#      | parent  | /asset/ELOUABT6       |
#      | target  | /tank/ELOUABT6.P-1-5  |
#      | tree    | Rosneft > Downstream > OAO Saratovskiy NPZ > ELOU-ABT-6 > Pech nagreva P-1-5 |
#    Then Asset with id "ELOUABT6.P-1-5" exists is true
#    And Asset with id "ELOUABT6.P-1-5" has "hide" : "true"
#
#  Scenario: [Asset] Update asset
#    Given Asset with id "ELOUABT6.P-1-5" exists is true
#    When Edit asset with
#      | uri     | /asset1/ELOUABT6.P-1-5 |
#      | classif | platform              |
#      | hide    | false                 |
#      | name    | ELOUABT6.P-1-5        |
#      | parent  | /asset/ELOUABT6       |
#      | target  | /tank/ELOUABT6.P-1-5  |
#      | tree    | Rosneft > Downstream > OAO Saratovskiy NPZ > ELOU-ABT-6 > Pech nagreva P-1-5 |
#    Then Asset with id "ELOUABT6.P-1-5" has "hide" : "false"
#    And Asset with id "ELOUABT6.P-1-5" has "classification" : "platform"
#
#  Scenario: [Asset] Delete asset
#    Given Asset with id "ELOUABT6.P-1-5" exists is true
#    When Delete asset with id "ELOUABT6.P-1-5"
#    Then Asset with id "ELOUABT6.P-1-5" exists is false
#
#  Scenario: [Asset] Return children
#    Given Asset with id "R.R1" exists is true
#    And Asset with id "R.R1" has "hasChildren" : "true"
#    And Asset with id "R.R1.XBO4C" has "parentId" : "R.R1"
#    Then Asset with id "R.R1" has children "R.R1.XBO4C"
#
#  Scenario: [Asset] Asset path
#    Then Asset with id "R.R1" has "path" : "[RN, RN.R, R.R1]"
#    Then Asset with id "R.R1.C003S" has "path" : "[RN, RN.R, R.R1, R.R1.C003S]"
#
  Scenario: [Asset] Get all assets as flat list
   When Get all assets as flat with pageNumber "2" and pageSize "4" result is by "id" count "4"
   When Get all assets as flat with pageNumber "2" and pageSize "4" result is by "name" count "4"



  Scenario: [Asset] Assets search flat
    When Create asset with
      | uri     | /asset/ASSET_TEST_1111-11|
      | classif | string                |
      | hide    | false                 |
      | name    | PATTERN_TEST          |
      | parent  | root                  |
      | target  | string                |
      | tree    | Rosneft > Downstream > OAO Testovii NPZ > TEST-ABT-6 > Pech testnagreva P-1-5 |
    When Create asset with
      | uri     | /asset/ASSET_TEST_2222-22  |
      | classif | string                     |
      | hide    | false                      |
      | name    | PATTERN2_TEST               |
      | parent  | /asset/ASSET_TEST_1111-11  |
      | target  | string                     |
      | tree    | string                     |
    When Create asset with
      | uri     | /asset/ASSET_TEST_33333-33|
      | classif | string                    |
      | hide    | false                     |
      | name    | OTHER_NAME                |
      | parent  | root                      |
      | target  | string                    |
      | tree    | string                    |
    Then Search flat with assetId "ASSET_TEST_1111-11" is result searchId "ASSET_TEST_1111-11" totalElements "1"
    Then Search flat with assetId "ASSET_TEST_1111-11" and assetId2 "ASSET_TEST_2222-22" is result searchId "ASSET_TEST_1111-11, ASSET_TEST_2222-22" totalElements "2"
    Then Search flat with assetId "ASSET_TEST_1111" is result searchId "[]" totalElements "0"
    Then Search flat with namePattern "PATTERN_TEST" is result totalElements "1" and listId
     |ASSET_TEST_1111-11|

    Then Search flat with namePattern "PATT" is result totalElements "2" and listId
     |ASSET_TEST_1111-11|
     |ASSET_TEST_2222-22|
    Then Search flat with namePattern "PATTERN_TEST" assetId "ASSET_TEST_1111-11" is result searchId "ASSET_TEST_1111-11" totalElements "1"
    Then Search flat with namePattern "PATTERN_TEST" assetId "ASSET_TEST_1111" is result searchId "[]" totalElements "0"
    Then Search flat with namePattern "PATTERN_TEST" assetId "ASSET_TEST_1111-11" and assetId2 "ASSET_TEST_3333-33" is result searchId "ASSET_TEST_1111-11" totalElements "1"
    Then Search flat with namePattern "PATTER" assetId "ASSET_TEST_1111-11" is result searchId "ASSET_TEST_1111-11" totalElements "1"
    Then Search flat with assetId "ASSET_TEST_1111-11" and children "true" is result searchId "ASSET_TEST_1111-11, ASSET_TEST_2222-22" totalElements "2"
    Then Search flat with assetId "ASSET_TEST_1111-11" and children "false" is result searchId "ASSET_TEST_1111-11" totalElements "1"
    Then Search flat with namePattern "PATT" and children "true" is result searchId "ASSET_TEST_1111-11, ASSET_TEST_2222-22" totalElements "2"
    Then Search flat with namePattern "PATT" and children "false" is result searchId "ASSET_TEST_2222-22" totalElements "1"
    Then Search flat with namePattern "PATTERN_TEST" assetId "ASSET_TEST_1111-11" and children "true" is result searchId "ASSET_TEST_1111-11, ASSET_TEST_2222-22" totalElements "2"
    Then Search flat with namePattern "WRONGNAME" assetId "ASSET_TEST_1111-11" is result searchId "[]" totalElements "0"



  Scenario: [Asset] Assets search tree
    Then Search tree with assetId "ASSET_TEST_1111-11" is result searchId "ASSET_TEST_1111-11" and has't field "children"
    Then Search tree with assetId "ASSET_TEST_1111-11" and assetId2 "ASSET_TEST_2222-22" is result searchId "ASSET_TEST_1111-11" childrenId "ASSET_TEST_2222-22"
    Then Search tree with assetId "ASSET_TEST_1111" is result searchId " "
    Then Search tree with namePattern "PATTERN2_TEST" is result searchId "ASSET_TEST_1111-11" childrenId "ASSET_TEST_2222-22"
    Then Search tree with namePattern "PATT" is result searchId "ASSET_TEST_1111-11" childrenId "ASSET_TEST_2222-22"
    Then Search tree with namePattern "PATTERN_TEST" assetId "ASSET_TEST_1111-11" is result searchId "ASSET_TEST_1111-11" and has't field "children"
    Then Search tree with namePattern "PATTERN_TEST" assetId "ASSET_TEST_1111" is result searchId " "
    Then Search tree with namePattern "PATTERN_TEST" assetId "ASSET_TEST_1111-11" and assetId2 "ASSET_TEST_3333-33" is result searchId "ASSET_TEST_1111-11" and has't field "children"
    Then Search tree with namePattern "PATTER" assetId "ASSET_TEST_1111-11" is result searchId "ASSET_TEST_1111-11" and has't field "children"
    Then Search tree with assetId "ASSET_TEST_1111-11" and children "true" is result searchId "ASSET_TEST_1111-11" childrenId "ASSET_TEST_2222-22"
    Then Search tree with assetId "ASSET_TEST_1111-11" and children "false" is result has't field "children"
    Then Search tree with namePattern "PATT" and children "true" is result searchId "ASSET_TEST_1111-11" childrenId "ASSET_TEST_2222-22"
    Then Search tree with namePattern "PATT" and children "false" is result searchId "ASSET_TEST_1111-11" and has't field "children"
    Then Search tree with namePattern "PATTERN_TEST" assetId "ASSET_TEST_1111-11" and children "true" is result searchId "ASSET_TEST_1111-11" childrenId "ASSET_TEST_2222-22"
    Then Search tree with namePattern "WRONGNAME" assetId "ASSET_TEST_1111-11" is result searchId " "



#    Then Search tree with assetId "ASSET_TEST_2222-22" is result Asset with id "ASSET_TEST_2222-22" has "parentId" : "ASSET_TEST_1111-11"


    When Delete asset with id "ASSET_TEST_1111-11"
    When Delete asset with id "ASSET_TEST_2222-22"
    When Delete asset with id "ASSET_TEST_3333-33"

#  Scenario: [Asset] Assets partial bulk loading
#    Given Asset with id "asset-AT-1.Vyhod.339" exists is true
#    And Delete asset with id "asset-AT-1.Vyhod.339"
#    And Create asset with
#      | uri     | /asset/ELOUABT6.P-1-5b|
#      | classif | equipment             |
#      | hide    | true                  |
#      | name    | ELOUABT6.P-1-5b       |
#      | parent  | /asset/ELOUABT6       |
#      | target  | /tank/ELOUABT6.P-1-5b |
#      | tree    | Rosneft > Downstream > OAO Saratovskiy NPZ > ELOU-ABT-6 > Pech nagreva P-1-5b |
#    And Edit asset with
#      | uri     | /asset/BND_UDNG1_SYS001 |
#      | classif | equipment               |
#      | hide    | true                    |
#      | name    | BND_UDNG1_SYS001        |
#      | parent  | /asset/ELOUABT6         |
#      | target  | /tank/BND_UDNG1_SYS001  |
#      | tree    | Rosneft > Downstream > OAO Saratovskiy NPZ > ELOU-ABT-6 > Pech nagreva P-1-5b |
#    When Start assets loading from POA
#    And Wait assets are loaded
#    Then Asset with id "asset-AT-1.Vyhod.339" exists is true
#    Then Asset with id "ELOUABT6.P-1-5b" exists is false

#  Scenario: [Asset] Assets full loading
#    Given Remember number of assets
#    When Delete all assets
#    Then Number of assets should be 0
#    When Start assets loading from POA
#    And Wait assets are loaded
#    Then Compare number of assets with memory
#    And Number of assets should be 181940

#Scenario: [Asset] Check asset count POA
#  Given Count assets in POA
#  Then Compare files

#  Scenario: [Asset] Get root assets (API)
#    Then Asset with id "RN" is root asset
#    Then Asset with id "SN" is root asset

#    Scenario: [Asset] Get all assets as flat list
#      Given Remember number of assets
#      And Asset with id "AA-1.Vhod.111" exists is false
#      And Create asset with
#        | uri     | /asset/AA-1.Vhod.111 |
#        | classif | equipment             |
#        | hide    | true                  |
#        | name    | AA-1.Vhod.111        |
#        | parent  | /asset/ELOUABT6       |
#        | target  | /tank/ELOUABT6.P-1-5  |
#        | tree    | Rosneft > Downstream > OAO Saratovskiy NPZ > ELOU-ABT-6 > Pech nagreva P-1-5 |
#      When Asset with id "AA-1.Vhod.111" exists is true
#      Then Asset "AA-1.Vhod.111" on page "count" with 1 page size

#      When Delete asset with id "AA-1.Vhod.111"

