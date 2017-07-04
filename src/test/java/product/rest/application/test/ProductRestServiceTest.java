package product.rest.application.test;

/**
 * Created by ravikuwi on 7/3/2017.
 */


import io.restassured.http.ContentType;
import org.junit.Test;

import static io.restassured.RestAssured.expect;
import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;


public class ProductRestServiceTest  {



    @Test
    public void testAndroidTV() {
        expect().body("name", equalTo("Android TV")).when().get("http://localhost:8080/o/products/products/108");
    }


    @Test
    public void testProductServiceGetResponseCode(){
        given().when().get("http://localhost:8080/o/products/products").then().statusCode(200);
    }

    @Test
    public void testPostNewProduct(){
         int productId = given().body("{ \"name\" : \"Samsung Monitor\",  \"description\" : \"Samsung Monitor\",  \"price\" : \"200\",  \"sku\" : \"SKU878\",  \"ratings\" : \"3\" }").with().contentType(ContentType.JSON).when().post("http://localhost:8080/o/products/products").then().statusCode(200).extract().path("id");
         assert (productId>100);
    }


    @Test
    public void testUpdateProduct(){
        int productId = given().body("{ \"name\" : \"Apple Monitor\",  \"description\" : \"Apple Monitor\",  \"price\" : \"200\",  \"sku\" : \"SKU879\",  \"ratings\" : \"3\" }").with().contentType(ContentType.JSON).when().post("http://localhost:8080/o/products/products").then().statusCode(200).extract().path("id");
        given().body("{ \"id\" :"+ productId + ", \"name\" : \"Apple Monitor 2\",  \"description\" : \"Apple Monitor 2\",  \"price\" : \"210\",  \"sku\" : \"SKU879\",  \"ratings\" : \"3\" }").with().contentType(ContentType.JSON).when().put("http://localhost:8080/o/products/products").then().statusCode(200);
        expect().body("name", equalTo("Apple Monitor 2")).when().get("http://localhost:8080/o/products/products/"+productId);
    }


    @Test
    public void testDeleteProduct(){
        int productId = given().body("{ \"name\" : \"Samsung Monitor\",  \"description\" : \"Samsung Monitor\",  \"price\" : \"200\",  \"sku\" : \"SKU878\",  \"ratings\" : \"3\" }").with().contentType(ContentType.JSON).when().post("http://localhost:8080/o/products/products").then().statusCode(200).extract().path("id");
        given().body("").expect().statusCode(204).when().delete("http://localhost:8080/o/products/products/"+productId);
    }



}
