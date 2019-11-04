package br.leg.camara.indexacao.adaptadores.mongodb.execucao;

import br.leg.camara.indexacao.aplicacao.execucao.InformacoesExcecao;
import br.leg.camara.indexacao.aplicacao.execucao.StatusExecucao;
import br.leg.camara.indexacao.testes.ResourcesDeTeste;
import br.leg.camara.indexacao.testes.StatusExecucoesDeTeste;
import com.mongodb.DBObject;
import com.mongodb.util.JSON;
import org.json.JSONException;
import org.junit.Test;
import org.skyscreamer.jsonassert.JSONAssert;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class ConversorStatusExecucaoTest {

	private static final String JSON_EXECUCAO_SUCESSO = "/mongo/historico/sucesso.json";
	private static final String JSON_EXECUCAO_ERRO = "/mongo/historico/erro.json";

	@Test
	public void converterParaJsonSucesso() throws JSONException {
		testeConversaoParaJson(StatusExecucoesDeTeste.encerradoComSucesso(), JSON_EXECUCAO_SUCESSO);
	}

	@Test
	public void converterParaJsonErro() throws JSONException {
		testeConversaoParaJson(StatusExecucoesDeTeste.encerradoComErro(), JSON_EXECUCAO_ERRO);
	}

	private void testeConversaoParaJson(StatusExecucao status, String caminhoParaJsonDoClasspath) throws JSONException {
		ConversorStatusExecucaoParaDBObject conversor = new ConversorStatusExecucaoParaDBObject();

		DBObject json = conversor.convert(status);

		String jsonEsperado = ResourcesDeTeste.lerDoClasspathComoString(caminhoParaJsonDoClasspath);
		JSONAssert.assertEquals(jsonEsperado, json.toString(), true);
	}

	@Test
	public void converterJsonParaStatusExecucaoComSucesso() {
		testeConversaoParaStatusExecucao(JSON_EXECUCAO_SUCESSO, StatusExecucoesDeTeste.encerradoComSucesso());
	}

	@Test
	public void converterJsonParaStatusExecucaoComErro() {
		testeConversaoParaStatusExecucao(JSON_EXECUCAO_ERRO, StatusExecucoesDeTeste.encerradoComErro());
	}

	private void testeConversaoParaStatusExecucao(String caminhoParaJsonDoClasspath, StatusExecucao statusEsperado) {
		DBObject dbObject = (DBObject) JSON.parse(ResourcesDeTeste.lerDoClasspathComoString(caminhoParaJsonDoClasspath));
		ConversorDBObjectParaStatusExecucao conversor = new ConversorDBObjectParaStatusExecucao();

		StatusExecucao status = conversor.convert(dbObject);

		assertEquals(statusEsperado, status);
		assertEquals(statusEsperado.getId(), status.getId());
		assertEquals(statusEsperado.getNomeJob(), status.getNomeJob());
		assertEquals(statusEsperado.getMensagem(), status.getMensagem());
		assertEquals(statusEsperado.getTotalDeDocumentos(), status.getTotalDeDocumentos());
		assertEquals(statusEsperado.getDocumentosLidos(), status.getDocumentosLidos());
		assertEquals(statusEsperado.getDocumentosIndexados(), status.getDocumentosIndexados());
		assertEquals(statusEsperado.getTimestampInicio(), status.getTimestampInicio());
		assertEquals(statusEsperado.getTimestampTermino(), status.getTimestampTermino());
		assertEquals(statusEsperado.getErros().size(), status.getErros().size());

		for (InformacoesExcecao erro : status.getErros()) {
			assertTrue("Erro não encontrado: " + erro, statusEsperado.getErros().contains(erro));
		}
	}
}
