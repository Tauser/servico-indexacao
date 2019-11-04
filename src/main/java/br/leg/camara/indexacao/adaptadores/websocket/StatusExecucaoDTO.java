package br.leg.camara.indexacao.adaptadores.websocket;

import br.leg.camara.indexacao.aplicacao.execucao.StatusExecucao;
import lombok.Data;
import org.springframework.web.util.HtmlUtils;

@Data
class StatusExecucaoDTO {

	private String id;
	private String nomeJob;
	private String parametros;
	private long totalDeDocumentos;
	private long documentosLidos;
	private long documentosIndexados;
	private String mensagem;

	StatusExecucaoDTO(StatusExecucao status) {
		//escape necessário para todas as Strings, caso contrário estará sujeito a falha de segurança
		this.id = HtmlUtils.htmlEscape(status.getId());
		this.nomeJob = HtmlUtils.htmlEscape(status.getNomeJob());
		this.parametros = HtmlUtils.htmlEscape(status.getParametrosExecucao().toString());
		this.totalDeDocumentos = status.getTotalDeDocumentos();
		this.documentosLidos = status.getDocumentosLidos();
		this.documentosIndexados = status.getDocumentosIndexados();
		this.mensagem = HtmlUtils.htmlEscape(status.getMensagem());
	}
}
