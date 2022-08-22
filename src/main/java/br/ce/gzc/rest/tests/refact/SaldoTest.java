package br.ce.gzc.rest.tests.refact;

import br.ce.gzc.rest.core.BaseTest;
import br.ce.gzc.rest.utils.BarrigaUtils;
import org.hamcrest.Matchers;
import org.junit.Test;

import static io.restassured.RestAssured.given;

public class SaldoTest extends BaseTest {

    @Test
    public void deveCalcularSaldContas() {
        Integer CONTA_ID = BarrigaUtils.getIdContaPeloNome("Conta para saldo");

        given()
        .when()
                .get("/saldo")
        .then()
                .statusCode(200)
                .body("find{it.conta_id == "+CONTA_ID+"}.saldo", Matchers.is("534.00"));
    }

}

