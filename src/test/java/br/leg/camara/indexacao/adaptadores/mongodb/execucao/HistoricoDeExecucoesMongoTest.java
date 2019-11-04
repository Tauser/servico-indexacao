package br.leg.camara.indexacao.adaptadores.mongodb.execucao;

import br.leg.camara.indexacao.adaptadores.mongodb.TesteDeIntegracaoComMongoDB;
import br.leg.camara.indexacao.aplicacao.execucao.StatusExecucao;
import br.leg.camara.indexacao.testes.StatusExecucoesDeTeste;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;

import static br.leg.camara.indexacao.adaptadores.mongodb.execucao.HistoricoDeExecucoesMongo.NOME_COLECAO;
import static org.junit.Assert.assertEquals;

public class HistoricoDeExecucoesMongoTest extends TesteDeIntegracaoComMongoDB {

	private HistoricoDeExecucoesMongo historico;

	@Before
	public void criarRepositorio() {
		historico = new HistoricoDeExecucoesMongo(mongoTemplate);
	}

	@After
	public void removerDocumentos() {
		removerTodosDocumentosDaColecao(NOME_COLECAO);
	}

	@Test
	public void adicionarStatusSucesso() {
		testeAdicionar(StatusExecucoesDeTeste.encerradoComSucesso());
	}

	@Test
	public void adicionarStatusComExcecao() {
		testeAdicionar(StatusExecucoesDeTeste.encerradoComErro());
	}

	private void testeAdicionar(StatusExecucao status) {
		long countAntes = quantidadeDeDocumentosDaColecao(NOME_COLECAO);

		historico.adicionar(status);

		long countDepois = quantidadeDeDocumentosDaColecao(NOME_COLECAO);
		assertEquals(countAntes + 1, countDepois);
	}

	@Test
	public void listarExecucoesDoDia() {
		StatusExecucao esperado = StatusExecucoesDeTeste.encerradoComErro();
		StatusExecucao outroStatus = StatusExecucoesDeTeste.encerradoComSucesso();
		historico.adicionar(esperado);
		historico.adicionar(outroStatus);

		Instant dataHora = esperado.getTimestampInicio();
		LocalDate dataLocal = dataHora.atZone(ZoneId.of("America/Sao_Paulo")).toLocalDate();

		List<StatusExecucao> encontrados = historico.listarExecucoesDoDia(dataLocal);

		assertEquals(1,  encontrados.size());

		StatusExecucao encontrado = encontrados.get(0);
		assertEquals(esperado.getId(), encontrado.getId());
		assertEquals(esperado.getNomeJob(), encontrado.getNomeJob());
		assertEquals(esperado.getMensagem(), encontrado.getMensagem());
		assertEquals(esperado.getTotalDeDocumentos(), encontrado.getTotalDeDocumentos());
		assertEquals(esperado.getDocumentosIndexados(), encontrado.getDocumentosIndexados());
		assertEquals(esperado.getDocumentosLidos(), encontrado.getDocumentosLidos());
		assertEquals(esperado.getTimestampInicio(), encontrado.getTimestampInicio());
		assertEquals(esperado.getTimestampTermino(), encontrado.getTimestampTermino());
		assertEquals(esperado.getErros(), encontrado.getErros());
	}

	@Test
	public void listarUltimasDoJob() {
		historico.adicionar(StatusExecucoesDeTeste.encerradoComSucesso());
		historico.adicionar(StatusExecucoesDeTeste.encerradoComErro());
		StatusExecucao esperado = StatusExecucoesDeTeste.encerradoComSucessoDiferente();
		historico.adicionar(esperado);

		List<StatusExecucao> historicos = historico.listarUltimasDoJob(esperado.getNomeJob());

		assertEquals(1, historicos.size());
		assertEquals(esperado, historicos.get(0));
	}
}