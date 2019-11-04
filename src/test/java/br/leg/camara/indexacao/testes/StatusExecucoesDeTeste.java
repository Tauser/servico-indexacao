package br.leg.camara.indexacao.testes;

import br.leg.camara.indexacao.api.ParametrosExecucao;
import br.leg.camara.indexacao.aplicacao.execucao.InformacoesExcecao;
import br.leg.camara.indexacao.aplicacao.execucao.StatusExecucao;

import static java.util.Collections.singletonList;

public class StatusExecucoesDeTeste {

	public static StatusExecucao encerradoComSucesso() {
		return StatusExecucao.builder()
				.id("1")
				.nomeJob("job-teste")
				.parametrosExecucao(ParametrosExecucao.vazio())
				.totalDeDocumentos(10)
				.documentosIndexados(10)
				.documentosLidos(10)
				.timestampInicio(DataHoraUtil.parse("01/01/2018 10:25:00"))
				.timestampTermino(DataHoraUtil.parse("01/01/2018 10:25:10"))
				.build();
	}

	public static StatusExecucao encerradoComErro() {
		return StatusExecucao.builder()
				.id("2")
				.nomeJob("job-teste")
				.parametrosExecucao(ParametrosExecucao.comValorSimples("id", "1"))
				.totalDeDocumentos(10)
				.documentosIndexados(5)
				.documentosLidos(10)
				.timestampInicio(DataHoraUtil.parse("05/01/2018 10:25:00"))
				.timestampTermino(DataHoraUtil.parse("05/01/2018 10:25:10"))
				.mensagem("Erro na execução")
				.erros(singletonList(InformacoesExcecao.builder()
						.stackTrace("java.lang.RuntimeException: Erro qualquer")
						.classeDaExcecao("java.lang.RuntimeException")
						.mensagemErro("Erro qualquer")
						.build()))
				.build();
	}

	public static StatusExecucao encerradoComSucessoDiferente() {
		return StatusExecucao.builder()
				.id("3")
				.nomeJob("job-outro-teste")
				.parametrosExecucao(ParametrosExecucao.vazio())
				.totalDeDocumentos(5)
				.documentosIndexados(5)
				.documentosLidos(5)
				.timestampInicio(DataHoraUtil.parse("07/01/2018 10:00:00"))
				.timestampTermino(DataHoraUtil.parse("07/01/2018 10:00:10"))
				.build();
	}
}
