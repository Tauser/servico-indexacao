package br.leg.camara.indexacao.adaptadores.mvc;

import br.leg.camara.indexacao.api.JobDeIndexacao;
import br.leg.camara.indexacao.aplicacao.RepositorioDeJobsDeIndexacao;
import br.leg.camara.indexacao.aplicacao.agendamento.Agendamento;
import br.leg.camara.indexacao.aplicacao.agendamento.ServicoDeAgendamentos;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

import static java.util.stream.Collectors.toList;

@AllArgsConstructor
@Controller
public class AgendamentosController {

	private static final String TEMPLATE_NOVO_AGENDAMENTO = "agendamentos/novo";

	@NonNull
	private final ServicoDeAgendamentos servicoDeAgendamentos;
	@NonNull
	private final RepositorioDeJobsDeIndexacao repositorioDeJobsDeIndexacao;

	@GetMapping(Urls.LISTAR_AGENDAMENTOS)
	public String listarTodosAgendamentos(Model model) {
		model.addAttribute("agendamentos", servicoDeAgendamentos.listarTodos());
		return "agendamentos/lista";
	}

	@GetMapping(Urls.NOVO_AGENDAMENTO)
	public String novoAgendamento(Model model) {
		return renderizarTelaNovoAgendamento(model, new FormularioAgendamento());
	}

	private String renderizarTelaNovoAgendamento(Model model, FormularioAgendamento form) {
		List<JobDeIndexacao> jobsDisponiveis = repositorioDeJobsDeIndexacao.listarTodos();
		List<String> nomesDosJobs = jobsDisponiveis.stream().map(JobDeIndexacao::nome).collect(toList());
		List<String> nomesDosIndices = jobsDisponiveis.stream().map(JobDeIndexacao::nomeDoIndice).distinct().collect(toList());

		model.addAttribute("nomesDosJobs", nomesDosJobs);
		model.addAttribute("nomesDosIndices", nomesDosIndices);
		model.addAttribute("agendamento", form);
		return TEMPLATE_NOVO_AGENDAMENTO;
	}

	@PostMapping(Urls.NOVO_AGENDAMENTO)
	public String novoAgendamento(Model model, RedirectAttributes redirectAttributes, FormularioAgendamento dados) {
		try {
			Agendamento agendamento = dados.criarAgendamento();
			servicoDeAgendamentos.adicionar(agendamento);
		} catch (Exception e) {
			Mensagens.erro(model, e.getMessage());
			return renderizarTelaNovoAgendamento(model, dados);
		}
		Mensagens.sucesso(redirectAttributes, "Agendamento cadastrado");
		return "redirect:" + Urls.LISTAR_AGENDAMENTOS;
	}

	@PostMapping(Urls.EXCLUIR_AGENDAMENTOS)
	public String excluirAgendamento(@PathVariable("idAgendamento") String idAgendamento, RedirectAttributes redirectAttributes,
									 Model model) {

		try {
			servicoDeAgendamentos.remover(idAgendamento);
			Mensagens.sucesso(redirectAttributes, "Agendamento excluído");
		} catch (Exception e) {
			Mensagens.erro(model, e.getMessage());
			return listarTodosAgendamentos(model);
		}
		return "redirect:" + Urls.LISTAR_AGENDAMENTOS;
	}
}
