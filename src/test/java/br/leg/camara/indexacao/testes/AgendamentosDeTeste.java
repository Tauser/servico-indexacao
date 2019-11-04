package br.leg.camara.indexacao.testes;

import br.leg.camara.indexacao.api.ParametrosExecucao;
import br.leg.camara.indexacao.aplicacao.agendamento.Agendamento;

public class AgendamentosDeTeste {

	public static Agendamento comUmParametroSimples() {
		ParametrosExecucao parametros = ParametrosExecucao.comValorSimples("id", "5");
		Agendamento agendamento = Agendamento.doJob("teste", "0 0 9-17 * * MON-FRI", parametros);
		agendamento.setId("valor-simples");
		return agendamento;
	}

	public static Agendamento comUmValorSimplesEOutroMultiplo() {
		ParametrosExecucao parametros = ParametrosExecucao.comValorSimples("simples", "1")
				.adicionarValorMultiplo("multiplo", "a", "b", "c");
		Agendamento agendamento = Agendamento.doJob("teste-2", "0 0 9-17 * * MON-FRI", parametros);
		agendamento.setId("valor-multiplo");
		return agendamento;
	}

	public static Agendamento semParametros() {
		Agendamento agendamento = Agendamento.dosJobsComIndice("indice-1", "0 0 8-17 * * MON-FRI", ParametrosExecucao.vazio());
		agendamento.setId("sem-parametros");
		return agendamento;
	}
}
