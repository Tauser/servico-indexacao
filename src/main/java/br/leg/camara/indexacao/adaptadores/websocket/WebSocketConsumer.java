package br.leg.camara.indexacao.adaptadores.websocket;

import br.leg.camara.indexacao.aplicacao.execucao.StatusExecucao;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import org.springframework.messaging.simp.SimpMessagingTemplate;

import java.util.function.Consumer;

@AllArgsConstructor
public class WebSocketConsumer implements Consumer<StatusExecucao> {

	@NonNull
	private final SimpMessagingTemplate template;

	@Override
	public void accept(StatusExecucao status) {
		template.convertAndSend("/topicos/status", new StatusExecucaoDTO(status));
	}
}
