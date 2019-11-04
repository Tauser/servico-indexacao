package br.leg.camara.indexacao.adaptadores.rest.dtos;

import br.leg.camara.indexacao.adaptadores.classloader.JobDeIndexacaoMock;
import br.leg.camara.indexacao.api.JobDeIndexacao;
import br.leg.camara.indexacao.api.ParametrosExecucao;
import br.leg.camara.indexacao.aplicacao.agendamento.Agendamento;
import br.leg.camara.indexacao.aplicacao.execucao.InformacoesExcecao;
import br.leg.camara.indexacao.aplicacao.execucao.StatusExecucao;
import br.leg.camara.indexacao.aplicacao.util.TimeUtils;
import br.leg.camara.indexacao.testes.AgendamentosDeTeste;
import br.leg.camara.indexacao.testes.StatusExecucoesDeTeste;
import org.junit.Test;

import java.util.List;
import java.util.Map;

import static org.junit.Assert.*;

public class ConversorDTOsTest {

	private final ConversorDTOs conversor = new ConversorDTOs();

	@Test
	public void converterJobDeIndexacao() {
		JobDeIndexacao job = new JobDeIndexacaoMock();
		DadosJob dados = conversor.converter(job, DadosJob.class);

		assertEquals(job.nome(), dados.getNome());
		assertEquals(job.nomeDoIndice(), dados.getNomeDoIndice());
	}

	@Test
	public void converterAgendamento() {
		Agendamento agendamento = AgendamentosDeTeste.comUmValorSimplesEOutroMultiplo();
		DadosAgendamento dados = conversor.converter(agendamento, DadosAgendamento.class);

		assertEquals(agendamento.getExpressaoCron(), dados.getExpressaoCron());
		assertEquals(agendamento.getNomeDoJob(), dados.getNomeDoJob());
		assertEquals(agendamento.getNomeDoIndice(), dados.getNomeDoIndice());
		assertEquals(agendamento.getId(), dados.getId());
		verificarParametros(agendamento.getParametros(), dados.getParametros());
	}

	private void verificarParametros(ParametrosExecucao params, Map<String, String[]> dtoParams) {
		assertNotNull(dtoParams);
		params.forEach((chave, valores) -> {
			assertTrue("Propriedade não encontrada" + chave, dtoParams.containsKey(chave));
			assertArrayEquals(valores, dtoParams.get(chave));
		});
	}

	@Test
	public void converterStatusExecucao() {
		StatusExecucao status = StatusExecucoesDeTeste.encerradoComErro();
		DadosExecucao dto = conversor.converter(status, DadosExecucao.class);

		assertEquals(status.getId(), dto.getId());
		assertEquals(status.getNomeJob(), dto.getNomeJob());
		assertEquals(TimeUtils.formatarTimestamp(status.getTimestampInicio()), dto.getTimestampInicio());
		assertEquals(TimeUtils.formatarTimestamp(status.getTimestampTermino()), dto.getTimestampTermino());
		assertEquals(status.getTotalDeDocumentos(), dto.getTotalDeDocumentos());
		assertEquals(status.getDocumentosLidos(), dto.getDocumentosLidos());
		assertEquals(status.getDocumentosIndexados(), dto.getDocumentosIndexados());
		assertEquals(status.getMensagem(), dto.getMensagem());
		verificarParametros(status.getParametrosExecucao(), dto.getParametrosExecucao());
		verificarErros(status.getErros(), dto.getErros());
	}

	private void verificarErros(List<InformacoesExcecao> erros, List<DadosExcecao> dtoErros) {
		assertNotNull(dtoErros);
		assertEquals(erros.size(), dtoErros.size());

		for (int i = 0; i < erros.size(); i++) {
			InformacoesExcecao info = erros.get(i);
			DadosExcecao dto = dtoErros.get(i);
			assertEquals(info.getClasseDaExcecao(), dto.getClasse());
			assertEquals(info.getMensagemErro(), dto.getMensagemErro());
			assertEquals(info.getStackTrace(), dto.getStackTrace());
		}
	}
}