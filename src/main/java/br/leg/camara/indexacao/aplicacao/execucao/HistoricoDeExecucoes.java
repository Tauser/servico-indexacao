package br.leg.camara.indexacao.aplicacao.execucao;

import java.time.LocalDate;
import java.util.List;

/**
 * Repositório de execuções encerradas. Execuções em andamento não aparecem aqui
 */
public interface HistoricoDeExecucoes {

	void adicionar(StatusExecucao status);

	List<StatusExecucao> listarExecucoesDoDia(LocalDate dataLocal);

	List<StatusExecucao> listarUltimasDoJob(String nomeDoJob);
}
