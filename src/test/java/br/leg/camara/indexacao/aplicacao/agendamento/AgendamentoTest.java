package br.leg.camara.indexacao.aplicacao.agendamento;

import br.leg.camara.indexacao.api.ParametrosExecucao;
import br.leg.camara.indexacao.testes.AgendamentosDeTeste;
import org.junit.Test;

import static org.junit.Assert.*;

public class AgendamentoTest {

	@Test(expected = IllegalArgumentException.class)
	public void agendamentoDeveTerUmNomeDeJobOuIndice() {
		Agendamento.builder()
				.expressaoCron("0 0 9-17 * * MON-FRI")
				.parametros(ParametrosExecucao.vazio())
				.build();
	}

	@Test(expected = IllegalArgumentException.class)
	public void agendamentoNaoDeveTerNomesDeJobEIndiceSimultaneamente() {
		Agendamento.builder()
				.nomeDoJob("job")
				.nomeDoIndice("indice")
				.expressaoCron("0 0 9-17 * * MON-FRI")
				.parametros(ParametrosExecucao.vazio())
				.build();
	}

	@Test(expected = IllegalArgumentException.class)
	public void agendamentoDevePossuirExpressoCron() {
		Agendamento.builder()
				.nomeDoJob("job")
				.parametros(ParametrosExecucao.vazio())
				.build();
	}

	@Test
	public void agendamentoNaoPrecisaDeParametros() {
		Agendamento agendamento = Agendamento.builder()
				.nomeDoJob("job")
				.expressaoCron("0 0 9-17 * * MON-FRI")
				.build();
		assertNotNull(agendamento);
	}

	@Test
	public void agendamentoDeJob() {
		Agendamento agendamento = Agendamento.builder()
				.nomeDoJob("job")
				.expressaoCron("0 0 9-17 * * MON-FRI")
				.parametros(ParametrosExecucao.vazio())
				.build();
		assertNotNull(agendamento);
	}

	@Test
	public void agendamentoDeJobsDoIndice() {
		Agendamento agendamento = Agendamento.builder()
				.nomeDoIndice("índice")
				.expressaoCron("0 0 9-17 * * MON-FRI")
				.parametros(ParametrosExecucao.vazio())
				.build();
		assertNotNull(agendamento);
	}
}