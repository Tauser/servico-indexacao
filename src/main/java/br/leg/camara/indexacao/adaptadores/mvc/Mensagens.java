package br.leg.camara.indexacao.adaptadores.mvc;

import org.springframework.ui.Model;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 * Helper que adiciona mensagens no Flash/Request Scope para serem mostradas no template da página
 */
class Mensagens {

	private static final String INFORMACAO = "mensagemInformacao";
	private static final String SUCESSO = "mensagemSucesso";
	private static final String ALERTA = "mensagemAlerta";
	private static final String ERRO = "mensagemErro";

	static void sucesso(RedirectAttributes redirectAttributes, String mensagem) {
		redirectAttributes.addFlashAttribute(SUCESSO, mensagem);
	}

	static void alerta(RedirectAttributes redirectAttributes, String mensagem) {
		redirectAttributes.addFlashAttribute(ALERTA, mensagem);
	}

	static void erro(RedirectAttributes redirectAttributes, String mensagem) {
		redirectAttributes.addFlashAttribute(ERRO, mensagem);
	}

	static void informacao(Model model, String mensagem) {
		model.addAttribute(INFORMACAO, mensagem);
	}

	static void sucesso(Model model, String mensagem) {
		model.addAttribute(SUCESSO, mensagem);
	}

	static void alerta(Model model, String mensagem) {
		model.addAttribute(ALERTA, mensagem);
	}

	static void erro(Model model, String mensagem) {
		model.addAttribute(ERRO, mensagem);
	}
}
