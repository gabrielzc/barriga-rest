package br.ce.gzc.rest.tests;

import br.ce.gzc.rest.core.BaseTest;
import org.junit.Test;

import static io.restassured.RestAssured.given;

public class BarrigaTest extends BaseTest {
    @Test
    public void naoDeveAcessarAPISemToken() {
        given()
        .when()
                .get("/contas")
        .then()
                .statusCode(401);
    }
}
