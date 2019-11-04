package br.leg.camara.indexacao.adaptadores.mvc;

import br.leg.camara.indexacao.api.JobDeIndexacao;
import br.leg.camara.indexacao.aplicacao.ServicoDeIndexacao;
import br.leg.camara.indexacao.aplicacao.execucao.ServicoDeExecucaoDeIndexacoes;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.*;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toSet;

@AllArgsConstructor
@Controller
public class PaginaInicialController {

	@NonNull
	private final ServicoDeExecucaoDeIndexacoes servico;
	@NonNull
	private final ServicoDeIndexacao servicoIndexacao;

	@GetMapping(Urls.PAGINA_INICIAL)
	public String index(Model model) {
		List<String> indices = servicoIndexacao.listarIndices();
		List<JobDeIndexacao> jobs = servico.listarJobsDisponiveis();

		Set<String> indicesAindaNaoCriados = jobs.stream()
				.map(JobDeIndexacao::nomeDoIndice)
				.filter(indice -> !indices.contains(indice))
				.collect(toSet());
		indices.addAll(indicesAindaNaoCriados);

		Map<String, List<JobDeIndexacao>> indicesJobs = new HashMap<>();
		Map<String, Integer> jobCounts = new HashMap<>();

		// Agrupa jobs de cada indice
		for (String indice: indices) {
			List<JobDeIndexacao> jobsDoIndice = jobs.stream()
					.filter(x -> x.nomeDoIndice().equals(indice))
					.collect(toList());
			indicesJobs.put(indice, jobsDoIndice);
			jobCounts.put(indice, jobsDoIndice.size());
		}

		// Ordena indices de acordo com o número de jobs
		Map<String, List<JobDeIndexacao>> orderedIndices = new LinkedHashMap<>();
		Stream<Map.Entry<String, Integer>> sequentialStream = jobCounts.entrySet().stream();
		Comparator<? super Map.Entry<String, Integer>> comparator = Map.Entry.comparingByValue();
		sequentialStream.sorted(comparator.reversed())
				.forEachOrdered(c -> orderedIndices.put(c.getKey(), indicesJobs.get(c.getKey())));

		model.addAttribute("indices", orderedIndices);
		return "index";
	}

	@PostMapping(Urls.EXECUTAR_JOB)
	public String executarJob(@PathVariable("nomeJob") String nomeJob) {
		servico.executar(nomeJob);
		return "redirect:" + Urls.PAGINA_INICIAL;
	}
}
