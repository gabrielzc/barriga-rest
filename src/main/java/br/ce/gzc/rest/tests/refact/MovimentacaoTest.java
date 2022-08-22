package br.ce.gzc.rest.tests.refact;

import br.ce.gzc.rest.core.BaseTest;
import br.ce.gzc.rest.tests.Movimentacao;
import br.ce.gzc.rest.utils.BarrigaUtils;
import br.ce.gzc.rest.utils.DataUtils;
import org.hamcrest.Matchers;
import org.junit.Test;

import static io.restassured.RestAssured.given;

public class MovimentacaoTest extends BaseTest {

    private Movimentacao getMovimentacaovalida() {
        Movimentacao mov = new Movimentacao();
        mov.setConta_id((BarrigaUtils.getIdContaPeloNome("Conta para movimentacoes")));
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
    public void deveInserirMovimentacaoComSucesso() {
        Movimentacao mov = getMovimentacaovalida();

        given()
                .body(mov)
        .when()
                .post("/transacoes")
        .then()
                .statusCode(201);
    }

    @Test
    public void deveValidarCamposObrigatoriosMovimentacao() {
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
    public void naoDeveInserirMovimetacaoDataFutura() {
        Movimentacao mov = getMovimentacaovalida();
        mov.setData_transacao(DataUtils.getDataDiferencaDias(2));
        given()
                .body(mov)
        .when()
                .post("/transacoes")
        .then()
                .statusCode(400)
                .body("$", Matchers.hasSize(1))
                .body("msg", Matchers.hasItem("Data da Movimentação deve ser menor ou igual à data atual"));
    }

    @Test
    public void naoDeveRemoverContaComMovimentacao() {
        Integer CONTA_ID = BarrigaUtils.getIdContaPeloNome("Conta com movimentacao");

        given()
                .pathParam("id", CONTA_ID)
        .when()
                .delete("/contas/{id}")
        .then()
                .statusCode(500)
                .body("constraint", Matchers.is("transacoes_conta_id_foreign"));
    }

    @Test
    public void deveRemoverMovimentacao() {
        Integer MOV_ID = BarrigaUtils.getIdMovimentacaoPelaDescricao("Movimentacao para exclusao");

        given()
                .pathParam("id", MOV_ID)
        .when()
                .delete("/transacoes/{id}")
        .then()
                .statusCode(204);
    }
}

