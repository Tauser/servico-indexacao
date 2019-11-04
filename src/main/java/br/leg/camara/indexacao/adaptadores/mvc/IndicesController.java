package br.leg.camara.indexacao.adaptadores.mvc;

import br.leg.camara.indexacao.aplicacao.ServicoDeIndexacao;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@AllArgsConstructor
public class IndicesController {

	private final ServicoDeIndexacao servicoDeIndexacao;

	@GetMapping(Urls.LISTAR_INDICES)
	public String listarIndices(Model model) {
		model.addAttribute("indices", servicoDeIndexacao.listarIndices());
		return "indices/lista";
	}

	@PostMapping(Urls.EXCLUIR_INDICE)
	public String removerIndice(@PathVariable("nomeDoIndice") String nomeDoIndice, Model model, RedirectAttributes redirectAttributes) {
		try {
			servicoDeIndexacao.removerIndice(nomeDoIndice);
			Mensagens.sucesso(redirectAttributes, "Índice excluído");
		} catch (Exception e) {
			Mensagens.erro(model, e.getMessage());
			return listarIndices(model);
		}
		return "redirect:" + Urls.LISTAR_INDICES;
	}

	@GetMapping(Urls.NOVO_INDICE)
	public String novoIndice(Model model) {
		return renderizarTelaNovoIndice(model, new FormularioIndice());
	}

	private String renderizarTelaNovoIndice(Model model, FormularioIndice dados) {
		model.addAttribute("indice", dados);
		return "indices/novo";
	}

	@PostMapping(Urls.NOVO_INDICE)
	public String salvarNovoIndice(Model model, RedirectAttributes redirectAttributes, FormularioIndice dados) {
		try {
			servicoDeIndexacao.criarIndice(dados.getNome(), dados.getConfiguracoes());
		} catch (Exception e) {
			Mensagens.erro(model, e.getMessage());
			return renderizarTelaNovoIndice(model, dados);
		}
		Mensagens.sucesso(redirectAttributes, "Índice criado com sucesso");
		return "redirect:" + Urls.LISTAR_INDICES;
	}

	@GetMapping(Urls.EXCLUIR_DOCUMENTO)
	public String renderizarExclusaoDeDocumento(@PathVariable("nomeDoIndice") String nomeDoIndice, Model model) {
		model.addAttribute("indice", nomeDoIndice);
		return "indices/exclusao-documento";
	}

	@PostMapping(Urls.EXCLUIR_DOCUMENTO)
	public String excluirDocumento(@PathVariable("nomeDoIndice") String nomeDoIndice, String idDocumento, Model model,
								   RedirectAttributes redirectAttributes) {
		try {
			servicoDeIndexacao.removerDocumento(nomeDoIndice, idDocumento);
			Mensagens.sucesso(redirectAttributes, "Documento excluído com sucesso");
			return "redirect:" + Urls.excluirDocumento(nomeDoIndice);
		} catch (Exception e) {
			Mensagens.erro(model, e.getMessage());
			return renderizarExclusaoDeDocumento(nomeDoIndice, model);
		}
	}
}
