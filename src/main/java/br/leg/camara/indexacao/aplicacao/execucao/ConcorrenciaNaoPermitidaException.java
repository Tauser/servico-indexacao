package br.leg.camara.indexacao.aplicacao.execucao;

/**
 * Indica a tentativa de execução concorrente de um job não thread-safe
 */
class ConcorrenciaNaoPermitidaException extends RuntimeException {

	ConcorrenciaNaoPermitidaException(String nomeDoJob) {
		super(String.format("O job %s já está em execução atualmente e não é thread-safe", nomeDoJob));

	}
}
