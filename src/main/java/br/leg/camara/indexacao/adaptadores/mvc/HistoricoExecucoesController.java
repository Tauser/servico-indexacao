package br.leg.camara.indexacao.adaptadores.mvc;

import br.leg.camara.indexacao.aplicacao.execucao.ServicoDeExecucaoDeIndexacoes;
import br.leg.camara.indexacao.aplicacao.execucao.StatusExecucao;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static com.google.common.base.Strings.isNullOrEmpty;

@Controller
@AllArgsConstructor
public class HistoricoExecucoesController {

	private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("dd-MM-yyyy");
	private static final String PARAM_DATA = "dia";

	@NonNull
	private final ServicoDeExecucaoDeIndexacoes servicoExecucoes;

	@GetMapping(Urls.HISTORICO_EXECUCOES)
	public String mostrarExecucoesDoDia(Model model, @RequestParam(name = PARAM_DATA, required = false) String dia) {

		LocalDate data = isNullOrEmpty(dia) ? LocalDate.now() : LocalDate.parse(dia, FORMATTER);
		List<StatusExecucao> historico = servicoExecucoes.listarExecucoesDoDia(data);

		model.addAttribute("data", data);
		model.addAttribute("urlDataAnterior", gerarUrlDoDia(data.minusDays(1)));
		model.addAttribute("urlDataSeguinte", gerarUrlDoDia(data.plusDays(1)));
		model.addAttribute("historico", historico);
		return "historico/lista";
	}

	private static String gerarUrlDoDia(LocalDate dia) {
		if (dia.isAfter(LocalDate.now())) {
			return null;
		}
		return Urls.HISTORICO_EXECUCOES + "?" + PARAM_DATA + "=" + FORMATTER.format(dia);
	}
}
