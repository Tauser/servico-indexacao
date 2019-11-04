package br.leg.camara.indexacao.aplicacao.execucao;

import br.leg.camara.indexacao.aplicacao.util.TimeUtils;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;

import java.time.Duration;
import java.util.function.Consumer;

/**
 * Loga no console e notifica listener
 */
@AllArgsConstructor
@Slf4j
class NotificadorExecucaoJob {

	@NonNull
	private final StatusExecucao status;
	@NonNull
	private final Consumer<StatusExecucao> listener;

	void info(String mensagemComFormatacao, Object... args) {
		String msg = String.format(mensagemComFormatacao, args);
		log.info(msg);
		status.alterarMensagem(msg);
		notificarListener();
	}

	void debug(String mensagemComFormatacao, Object... args) {
		String msg = String.format(mensagemComFormatacao, args);
		log.debug(msg);
		status.alterarMensagem(msg);
		notificarListener();
	}
	
	void erro(Exception erro, String mensagemComFormatacao, Object... args) {
		String msg = String.format(mensagemComFormatacao, args);
		log.error(msg, erro);
		status.registrarExcecao(erro, msg);
		notificarListener();
	}

	void infoTerminoJob() {
		long tempoEmMilis = status.getDuracaoEmMs();
		Duration duracao = Duration.ofMillis(status.getDuracaoEmMs());
		info("Job %s encerrado. %d docs em %s. Média: %.2f docs/s",
				status.getNomeJob(), status.getTotalDeDocumentos(), TimeUtils.formatarDuracao(duracao),
				((double) status.getTotalDeDocumentos()) / (tempoEmMilis/1000));
	}

	void notificarListener() {
		try {
			listener.accept(status);
		} catch (Exception e) {
			log.error("Erro ao notificar listener: " + e.getMessage(), e);
		}
	}
}
