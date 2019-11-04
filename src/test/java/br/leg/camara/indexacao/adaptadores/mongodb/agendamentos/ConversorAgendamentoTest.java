package br.leg.camara.indexacao.adaptadores.mongodb.agendamentos;

import br.leg.camara.indexacao.aplicacao.agendamento.Agendamento;
import br.leg.camara.indexacao.testes.AgendamentosDeTeste;
import br.leg.camara.indexacao.testes.ResourcesDeTeste;
import com.mongodb.DBObject;
import com.mongodb.util.JSON;
import org.json.JSONException;
import org.junit.Test;
import org.skyscreamer.jsonassert.JSONAssert;

import static org.junit.Assert.assertEquals;

public class ConversorAgendamentoTest {

	private static final String JSON_AGENDAMENTO_SEM_PARAMETROS = "/mongo/agendamentos/sem-parametros.json";
	private static final String JSON_AGENDAMENTO_VALOR_MULTIPLO = "/mongo/agendamentos/valor-multiplo.json";
	private static final String JSON_AGENDAMENTO_VALOR_SIMPLES = "/mongo/agendamentos/valor-simples.json";

	@Test
	public void converterParaJsonSemParametros() throws JSONException {
		testeConversaoParaJson(AgendamentosDeTeste.semParametros(), JSON_AGENDAMENTO_SEM_PARAMETROS);
	}

	@Test
	public void converterParaJsonComParametroDeValorMultiplo() throws JSONException {
		testeConversaoParaJson(AgendamentosDeTeste.comUmValorSimplesEOutroMultiplo(), JSON_AGENDAMENTO_VALOR_MULTIPLO);
	}

	@Test
	public void converterParaJsonComParametroDeValorSimples() throws JSONException {
		testeConversaoParaJson(AgendamentosDeTeste.comUmParametroSimples(), JSON_AGENDAMENTO_VALOR_SIMPLES);
	}

	private void testeConversaoParaJson(Agendamento agendamento, String caminhoParaJsonDoClasspath) throws JSONException {
		ConversorAgendamentoParaDBObject conversor = new ConversorAgendamentoParaDBObject();

		DBObject json = conversor.convert(agendamento);

		String jsonEsperado = ResourcesDeTeste.lerDoClasspathComoString(caminhoParaJsonDoClasspath);
		JSONAssert.assertEquals(jsonEsperado, json.toString(), true);
	}

	@Test
	public void converterJsonParaAgendamentoSemParametros() {
		testeConversaoParaAgendamento(JSON_AGENDAMENTO_SEM_PARAMETROS, AgendamentosDeTeste.semParametros());
	}

	@Test
	public void converterJsonParaAgendamentoComParametroSimples() {
		testeConversaoParaAgendamento(JSON_AGENDAMENTO_VALOR_SIMPLES, AgendamentosDeTeste.comUmParametroSimples());
	}

	@Test
	public void converterJsonParaAgendamentoComParametroDeValorMultiplo() {
		testeConversaoParaAgendamento(JSON_AGENDAMENTO_VALOR_MULTIPLO, AgendamentosDeTeste.comUmValorSimplesEOutroMultiplo());
	}

	private void testeConversaoParaAgendamento(String caminhoParaJsonDoClasspath, Agendamento agendamentoEsperado) {
		DBObject dbObject = (DBObject) JSON.parse(ResourcesDeTeste.lerDoClasspathComoString(caminhoParaJsonDoClasspath));
		ConversorDBObjectParaAgendamento conversor = new ConversorDBObjectParaAgendamento();

		Agendamento agendamento = conversor.convert(dbObject);

		assertEquals(agendamentoEsperado, agendamento);
		assertEquals(agendamentoEsperado.getId(), agendamento.getId());
		assertEquals(agendamentoEsperado.getNomeDoJob(), agendamento.getNomeDoJob());
		assertEquals(agendamentoEsperado.getNomeDoIndice(), agendamento.getNomeDoIndice());
		assertEquals(agendamentoEsperado.getExpressaoCron(), agendamento.getExpressaoCron());
		assertEquals(agendamentoEsperado.getParametros(), agendamento.getParametros());
	}
}
