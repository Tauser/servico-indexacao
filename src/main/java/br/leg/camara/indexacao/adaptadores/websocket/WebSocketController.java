package br.leg.camara.indexacao.adaptadores.websocket;

import br.leg.camara.indexacao.adaptadores.websocket.requisicoes.ExecucaoJob;
import br.leg.camara.indexacao.api.ParametrosExecucao;
import br.leg.camara.indexacao.aplicacao.execucao.ServicoDeExecucaoDeIndexacoes;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

@Controller
@AllArgsConstructor
public class WebSocketController {

	@NonNull
	private final ServicoDeExecucaoDeIndexacoes servico;
	@NonNull
	private final SimpMessagingTemplate template;

	@MessageMapping("/executarJob")
	public void executarJob(ExecucaoJob request) {
		servico.executar(request.getNomeDoJob(), new WebSocketConsumer(template), ParametrosExecucao.vazio());
	}
}
