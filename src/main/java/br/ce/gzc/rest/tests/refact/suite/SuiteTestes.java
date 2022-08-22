package br.ce.gzc.rest.tests.refact.suite;

import br.ce.gzc.rest.core.BaseTest;
import br.ce.gzc.rest.tests.refact.AuthTest;
import br.ce.gzc.rest.tests.refact.ContasTest;
import br.ce.gzc.rest.tests.refact.MovimentacaoTest;
import br.ce.gzc.rest.tests.refact.SaldoTest;
import io.restassured.RestAssured;
import org.junit.BeforeClass;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;

import java.util.HashMap;
import java.util.Map;

import static io.restassured.RestAssured.given;

@RunWith(org.junit.runners.Suite.class)
@Suite.SuiteClasses({
        ContasTest.class,
        MovimentacaoTest.class,
        SaldoTest.class,
        AuthTest.class
})

public class SuiteTestes extends BaseTest {
    // Before = executa antes de cada testes
    // BeforeClass = executa apenas uma vez pra classe inteira
    @BeforeClass
    public static void login() {
        Map<String, String> login = new HashMap<>();
        login.put("email", "gabrielzc@ciandt.com");
        login.put("senha", "123456");

        String TOKEN = given()
                .body(login)
                .when()
                .post("/signin")
                .then()
                .statusCode(200)
                .extract().path("token");

        RestAssured.requestSpecification.header("Authorization", "JWT " + TOKEN);

        RestAssured.get("/reset").then().statusCode(200);
    }
}
