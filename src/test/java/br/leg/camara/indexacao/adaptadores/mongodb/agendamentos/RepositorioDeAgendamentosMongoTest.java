package br.leg.camara.indexacao.adaptadores.mongodb.agendamentos;

import br.leg.camara.indexacao.adaptadores.mongodb.TesteDeIntegracaoComMongoDB;
import br.leg.camara.indexacao.aplicacao.agendamento.Agendamento;
import br.leg.camara.indexacao.testes.AgendamentosDeTeste;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static br.leg.camara.indexacao.adaptadores.mongodb.agendamentos.RepositorioDeAgendamentosMongo.NOME_COLECAO;
import static org.junit.Assert.*;

public class RepositorioDeAgendamentosMongoTest extends TesteDeIntegracaoComMongoDB {

	private RepositorioDeAgendamentosMongo repositorio;

	@Before
	public void criarRepositorio() {
		repositorio = new RepositorioDeAgendamentosMongo(mongoTemplate);
	}

	@After
	public void destroy () {
		removerTodosDocumentosDaColecao(NOME_COLECAO);
	}

	@Test
	public void adicionarDeveSetarIdDoAgendamento() {
		Agendamento agendamento = AgendamentosDeTeste.comUmParametroSimples();
		agendamento.setId(null);
		long countAntes = quantidadeDeDocumentosDaColecao(NOME_COLECAO);

		repositorio.adicionar(agendamento);

		assertNotNull(agendamento.getId());
		long countDepois = quantidadeDeDocumentosDaColecao(NOME_COLECAO);
		assertEquals(countAntes + 1, countDepois);
	}

	@Test(expected = IllegalArgumentException.class)
	public void adicionarDeveFalharSeAgendamentoTiverIdNaoNulo() {
		Agendamento agendamento = AgendamentosDeTeste.comUmParametroSimples();
		agendamento.setId("qualquer-coisa");

		repositorio.adicionar(agendamento);
	}

	@Test
	public void listarTodos() {
		List<String> idsAgendamentos = new ArrayList<>();
		idsAgendamentos.add(adicionarAgendamento(AgendamentosDeTeste.comUmParametroSimples()));
		idsAgendamentos.add(adicionarAgendamento(AgendamentosDeTeste.comUmValorSimplesEOutroMultiplo()));

		List<Agendamento> agendamentos = repositorio.listarTodos();

		assertEquals(idsAgendamentos.size(), agendamentos.size());
		for (Agendamento agendamento : agendamentos) {
			assertTrue("id não encontrado " + agendamento.getId(), idsAgendamentos.contains(agendamento.getId()));
		}
	}

	@Test
	public void testeMapeamento() {
		Agendamento esperado = AgendamentosDeTeste.comUmParametroSimples();
		String id = adicionarAgendamento(esperado);

		Agendamento agendamento = repositorio.buscarPorId(id);

		compararAgendamentos(esperado, agendamento);
	}

	private void compararAgendamentos(Agendamento esperado, Agendamento agendamento) {
		assertEquals(esperado.getId(), agendamento.getId());
		assertEquals(esperado.getNomeDoJob(), agendamento.getNomeDoJob());
		assertEquals(esperado.getNomeDoIndice(), agendamento.getNomeDoIndice());
		assertEquals(esperado.getExpressaoCron(), agendamento.getExpressaoCron());
		assertEquals(esperado.getParametros(), agendamento.getParametros());
	}

	@Test
	public void removerAgendamentoExistente() {
		String idASerRemovido = adicionarAgendamento(AgendamentosDeTeste.semParametros());
		String outroId = adicionarAgendamento(AgendamentosDeTeste.comUmParametroSimples());

		repositorio.remover(idASerRemovido);

		Agendamento agendamento = mongoTemplate.findById(idASerRemovido, Agendamento.class, NOME_COLECAO);
		assertNull(agendamento);

		Agendamento outroAgendamento = mongoTemplate.findById(outroId, Agendamento.class, NOME_COLECAO);
		assertNotNull(outroAgendamento);
	}

	@Test
	public void buscarPorIdAgendamentoExistente() {
		Agendamento esperado = AgendamentosDeTeste.semParametros();
		String id = adicionarAgendamento(esperado);

		Agendamento encontrado = repositorio.buscarPorId(id);

		compararAgendamentos(esperado, encontrado);
	}

	@Test
	public void buscarPorIdInexistenteDeveRetornarNull() {
		Agendamento agendamento = repositorio.buscarPorId("qualquer-coisa-que-nao-existe");
		assertNull(agendamento);
	}

	private String adicionarAgendamento(Agendamento agendamento) {
		agendamento.setId(null);
		repositorio.adicionar(agendamento);
		return agendamento.getId();
	}
}