package br.ce.gzc.rest.tests;

import br.ce.gzc.rest.core.BaseTest;
import br.ce.gzc.rest.utils.DataUtils;
import io.restassured.RestAssured;
import io.restassured.specification.FilterableRequestSpecification;
import org.hamcrest.Matchers;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import static io.restassured.RestAssured.given;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class BarrigaTest extends BaseTest {

    private static String CONTA_NAME = "Conta " + System.nanoTime();
    private static Integer CONTA_ID;
    private static Integer MOV_ID;

    private Movimentacao getMovimentacaovalida() {
        Movimentacao mov = new Movimentacao();
        mov.setConta_id((CONTA_ID));
        //mov.setUsuario_id();
        mov.setDescricao("Descrição da movimentação");
        mov.setEnvolvido("Envolvido na mov");
        mov.setTipo("REC");
        mov.setData_transacao(DataUtils.getDataDiferencaDias(-1));
        mov.setData_pagamento(DataUtils.getDataDiferencaDias(5));
        mov.setValor(100f);
        mov.setStatus(true);
        return mov;
    }

    @Test
    public void t11_naoDeveAcessarAPISemToken() {
        FilterableRequestSpecification req = (FilterableRequestSpecification) RestAssured.requestSpecification;
        req.removeHeader("Authorization");

        given()
        .when()
                .get("/contas")
        .then()
                .statusCode(401);
    }

    @Test
    public void t02_deveIncluirContaComSucesso() {
        CONTA_ID = given()
                .body("{\"nome\": \""+CONTA_NAME+"\"}")
        .when()
                .post("/contas")
        .then()
                .statusCode(201)
                .extract().path("id");
    }

    @Test
    public void t03_deveAlterarContaComSucesso() {
        given()
                .body("{\"nome\": \""+CONTA_NAME+" alterada\"}")
                .pathParam("id", CONTA_ID)
        .when()
                .put("/contas/{id}")
        .then()
                .statusCode(200)
                .body("nome", Matchers.is(CONTA_NAME+" alterada"));
    }

    @Test
    public void t04_naoDeveIncluirContaComMesmoNome() {
        given()
                .body("{\"nome\": \""+CONTA_NAME+" alterada\"}")
        .when()
                .post("/contas")
        .then()
                .statusCode(400)
                .body("error", Matchers.is("Já existe uma conta com esse nome!"));
    }

    @Test
    public void t05_deveInserirMovimentacaoComSucesso() {
        Movimentacao mov = getMovimentacaovalida();

        MOV_ID = given()
                .body(mov)
        .when()
                .post("/transacoes")
        .then()
                .statusCode(201)
                .extract().path("id");
    }

    @Test
    public void t06_deveValidarCamposObrigatoriosMovimentacao() {
        given()
                .body("{}")
        .when()
                .post("/transacoes")
        .then()
                .statusCode(400)
                .body("$", Matchers.hasSize(8))
                .body("msg", Matchers.hasItems("Data da Movimentação é obrigatório",
                        "Data do pagamento é obrigatório",
                        "Descrição é obrigatório",
                        "Interessado é obrigatório",
                        "Valor é obrigatório",
                        "Valor deve ser um número",
                        "Conta é obrigatório",
                        "Situação é obrigatório"));
    }

    @Test
    public void t07_naoDeveInserirMovimetacaoDataFutura() {
        Movimentacao mov = getMovimentacaovalida();
        mov.setData_transacao(DataUtils.getDataDiferencaDias(2));
        given()
                .body(mov)
        .when()
                .post("/transacoes")
        .then()
                .statusCode(400)
                .body("$", Matchers.hasSize(1))
                // is = quando tem apenas um valor
                // hasItem = quando vem dentro de um array
                .body("msg", Matchers.hasItem("Data da Movimentação deve ser menor ou igual à data atual"));
    }

    @Test
    public void t08_naoDeveRemoverContaComMovimentacao() {
        given()
                .pathParam("id", CONTA_ID)
        .when()
                .delete("/contas/{id}")
        .then()
                .statusCode(500)
                .body("constraint", Matchers.is("transacoes_conta_id_foreign"));
    }

    @Test
    public void t09_deveCalcularSaldContas() {
        given()
        .when()
                .get("/saldo")
        .then()
                .statusCode(200)
                .body("find{it.conta_id == "+CONTA_ID+"}.saldo", Matchers.is("100.00"));
    }

    @Test
    public void t10_deveRemoverMovimentacao() {
        given()
                .pathParam("id", MOV_ID)
        .when()
                .delete("/transacoes/{id}")
        .then()
                .statusCode(204);
    }

}
