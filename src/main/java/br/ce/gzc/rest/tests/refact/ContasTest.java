package br.ce.gzc.rest.tests.refact;

import br.ce.gzc.rest.core.BaseTest;
import br.ce.gzc.rest.utils.BarrigaUtils;
import org.hamcrest.Matchers;
import org.junit.Test;

import static io.restassured.RestAssured.given;

public class ContasTest extends BaseTest {

    @Test
    public void deveIncluirContaComSucesso() {
        given()
                .body("{\"nome\": \"Conta inserida\"}")
        .when()
                .post("/contas")
        .then()
                .statusCode(201);
    }

    @Test
    public void deveAlterarContaComSucesso() {
        Integer CONTA_ID = BarrigaUtils.getIdContaPeloNome("Conta para alterar");

        given()
                .body("{\"nome\": \"Conta alterada\"}")
                .pathParam("id", CONTA_ID)
        .when()
                .put("/contas/{id}")
        .then()
                .statusCode(200)
                .body("nome", Matchers.is("Conta alterada"));
    }

    @Test
    public void naoDeveIncluirContaComMesmoNome() {
        given()
                .body("{\"nome\": \"Conta mesmo nome\"}")
        .when()
                .post("/contas")
        .then()
                .statusCode(400)
                .body("error", Matchers.is("Já existe uma conta com esse nome!"));
    }
}

