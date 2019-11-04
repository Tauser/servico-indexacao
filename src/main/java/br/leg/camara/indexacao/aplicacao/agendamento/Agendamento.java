package br.leg.camara.indexacao.aplicacao.agendamento;

import br.leg.camara.indexacao.api.ParametrosExecucao;
import lombok.*;

import static com.google.common.base.Strings.isNullOrEmpty;

@Builder
@EqualsAndHashCode(of = "id")
@ToString
public class Agendamento {

	@Getter
	@Setter
	private String id;
	@Getter
	private final String expressaoCron;
	@Getter
	private final String nomeDoIndice;
	@Getter
	private final String nomeDoJob;
	private final ParametrosExecucao parametros;

	public static Agendamento dosJobsComIndice(String nomeDoIndice, String expressaoCron, ParametrosExecucao parametros) {
		return Agendamento.builder()
				.nomeDoIndice(nomeDoIndice)
				.expressaoCron(expressaoCron)
				.parametros(parametros)
				.build();
	}

	public static Agendamento doJob(String nomeDoJob, String expressaoCron, ParametrosExecucao parametros) {
		return Agendamento.builder()
				.nomeDoJob(nomeDoJob)
				.expressaoCron(expressaoCron)
				.parametros(parametros)
				.build();
	}

	public ParametrosExecucao getParametros() {
		//gera copia para que mudanças feitas posteriormente não modifiquem o agendamento original
		return parametros.gerarCopia();
	}

	public boolean deUmUnicoJob() {
		return nomeDoJob != null;
	}

	private Agendamento(String id, String expressaoCron, String nomeDoIndice, String nomeDoJob, ParametrosExecucao parametros) {
		if ((isNullOrEmpty(nomeDoIndice) && isNullOrEmpty(nomeDoJob)) || (!isNullOrEmpty(nomeDoIndice) && !isNullOrEmpty(nomeDoJob))) {
			throw new IllegalArgumentException("O agendamento deve possuir um nome de job ou de índice");
		}
		if (isNullOrEmpty(expressaoCron)) {
			throw new IllegalArgumentException("Expressão do agendamento deve ser informada");
		}
		this.id = id;
		this.expressaoCron = expressaoCron;
		this.nomeDoIndice = nomeDoIndice;
		this.nomeDoJob = nomeDoJob;
		this.parametros = parametros == null ? ParametrosExecucao.vazio() : parametros.gerarCopia();
	}
}
