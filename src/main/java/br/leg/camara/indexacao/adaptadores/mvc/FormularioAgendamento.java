package br.leg.camara.indexacao.adaptadores.mvc;

import br.leg.camara.indexacao.api.ParametrosExecucao;
import br.leg.camara.indexacao.aplicacao.agendamento.Agendamento;
import com.google.common.base.Splitter;
import lombok.Data;

import java.util.Map;

import static com.google.common.base.Strings.isNullOrEmpty;


@Data
public class FormularioAgendamento {

	public static final String SEPARADOR = "|";

	private String nomeDoJob;
	private String nomeDoIndice;
	private String expressaoCron;
	private Map<String, String> parametros;

	Agendamento criarAgendamento() {
		if ((!isNullOrEmpty(nomeDoJob)) && (!isNullOrEmpty(nomeDoIndice))) {
			throw new IllegalArgumentException("Você deve informar o nome do job OU o nome do índice");
		}
		if (isNullOrEmpty(nomeDoJob)) {
			return Agendamento.dosJobsComIndice(nomeDoIndice, expressaoCron, criarParametros());
		}
		return Agendamento.doJob(nomeDoJob, expressaoCron, criarParametros());
	}

	private ParametrosExecucao criarParametros() {
		ParametrosExecucao paramsExecucao = ParametrosExecucao.vazio();
		if (parametros == null || parametros.isEmpty()) {
			return paramsExecucao;
		}

		Splitter splitter = Splitter.on(SEPARADOR).omitEmptyStrings();
		parametros.forEach((chave, valor) -> {
			if (valor.contains(SEPARADOR)) {
				paramsExecucao.adicionarValorMultiplo(chave, splitter.splitToList(valor));
			} else {
				paramsExecucao.adicionarValorSimples(chave, valor);
			}
		});
		return paramsExecucao;
	}
}
