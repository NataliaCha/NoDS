package com.smartdigit.dev.generator.template;

import org.junit.Test;

import java.util.List;

import static io.restassured.RestAssured.get;
import static io.restassured.RestAssured.given;
import static io.restassured.path.xml.XmlPath.from;


public class restTest {

    String host = "https://time-series-store-predix.run.aws-usw02-pr.ice.predix.io/";
    String token = "Bearer eyJhbGciOiJSUzI1NiIsImtpZCI6IjBLV3cwIiwidHlwIjoiSldUIn0.eyJqdGkiOiJhOGIxYTBkZDAwODU0N2VkODNhNTljMTU3MjE4ZmEzMSIsInN1YiI6InRzIiwic2NvcGUiOlsiYXVkaXQuem9uZXMuMDExNGE1ZGYtYThhNC00NDY4LTgwYTUtNWI5ZTc0MjU2ZTY2LnVzZXIiLCJ1YWEubm9uZSIsInRpbWVzZXJpZXMuem9uZXMuMmM5YTFjYzItYzEwNS00NTJhLTgwMGEtYmJkMGE1ODk1MmZhLnVzZXIiLCJwcmVkaXgtZXZlbnQtaHViLnpvbmVzLjQ1MGJiZWYwLTY0NTUtNDhkOS1hMDBkLTU0ZmE0ZWJlY2JkNC51c2VyIiwidGltZXNlcmllcy56b25lcy4yYzlhMWNjMi1jMTA1LTQ1MmEtODAwYS1iYmQwYTU4OTUyZmEucXVlcnkiLCJwcmVkaXgtZXZlbnQtaHViLnpvbmVzLjQ1MGJiZWYwLTY0NTUtNDhkOS1hMDBkLTU0ZmE0ZWJlY2JkNC5ncnBjLnB1Ymxpc2giLCJ0aW1lc2VyaWVzLnpvbmVzLjJjOWExY2MyLWMxMDUtNDUyYS04MDBhLWJiZDBhNTg5NTJmYS5pbmdlc3QiXSwiY2xpZW50X2lkIjoidHMiLCJjaWQiOiJ0cyIsImF6cCI6InRzIiwiZ3JhbnRfdHlwZSI6ImNsaWVudF9jcmVkZW50aWFscyIsInJldl9zaWciOiJjZWRmYzMzIiwiaWF0IjoxNTQxNzUyOTA1LCJleHAiOjE1NDE3OTYxMDUsImlzcyI6Imh0dHBzOi8vMjdmMTdkZmMtMDc4ZS00YmZmLWFlMjMtYTZhNzUzY2E1NTFjLnByZWRpeC11YWEucnVuLmF3cy11c3cwMi1wci5pY2UucHJlZGl4LmlvL29hdXRoL3Rva2VuIiwiemlkIjoiMjdmMTdkZmMtMDc4ZS00YmZmLWFlMjMtYTZhNzUzY2E1NTFjIiwiYXVkIjpbInByZWRpeC1ldmVudC1odWIuem9uZXMuNDUwYmJlZjAtNjQ1NS00OGQ5LWEwMGQtNTRmYTRlYmVjYmQ0LmdycGMiLCJ0aW1lc2VyaWVzLnpvbmVzLjJjOWExY2MyLWMxMDUtNDUyYS04MDBhLWJiZDBhNTg5NTJmYSIsInByZWRpeC1ldmVudC1odWIuem9uZXMuNDUwYmJlZjAtNjQ1NS00OGQ5LWEwMGQtNTRmYTRlYmVjYmQ0IiwiYXVkaXQuem9uZXMuMDExNGE1ZGYtYThhNC00NDY4LTgwYTUtNWI5ZTc0MjU2ZTY2IiwidHMiXX0.erNp06u2HQrnseUJr9KjeWzMHC-PyrudCS0SrbDsqD5gpvmDCKSg8ovZtPgTuMAJspcyOOHyHiGPqwXE3fC30j73h_oZVyCCkoBastp5omD9af7tHv5AX1g6e8gb83QbaTTUyey5NTRbNxu4-DKBNwcdwKxIUFETTwwPwjYhVN_soNfVUertJ1WVEM308Efe4s7sQq5v51S1fykfSKmMcs0Nwdx1Hj7B63CudAS31sPnsApjv1xcjjUFdrFZs5cN0v3uHQsrBkFz1nImLWuUpfgY7sssZPXJbez8Wt-vJsdGeAgqAKAStGPvDwgh4q0Cw5fwGKozypc7BNZclQBGtg";

    @Test
    public void restFirstTest(){
        given()
                .contentType("application/json")
                .header("Predix-Zone-Id","2c9a1cc2-c105-452a-800a-bbd0a58952fa")
                .header("Authorization",token)
                .body("{\"start\": \"1mm-ago\",\"tags\": [{\"name\": \"SENSOR.PRESSURE.1\",\"limit\": 1000,\"aggregations\": [{\"type\":\"count\",\"sampling\":{\"datapoints\":1}}]}]}").
        post(host + "v1/datapoints").getBody().prettyPrint();//.then().assertThat().statusCode(200);

        String response = get("/shopping").asString();
        List<String> groceries = from(response).getList("shopping.category.find { it.@type == 'groceries' }.item");

    }

    @Test
    public void getTags(){
        given()
                .contentType("application/json")
                .header("Predix-Zone-Id","2c9a1cc2-c105-452a-800a-bbd0a58952fa")
                .header("Authorization",token).
                get(host + "v1/tags").getBody().prettyPrint();//.then().assertThat().statusCode(200);
    }
}
