package br.leg.camara.indexacao.adaptadores.classloader;

import br.leg.camara.indexacao.api.Configuracoes;
import br.leg.camara.indexacao.api.JobBuscadorDeJobs;
import br.leg.camara.indexacao.api.JobDeIndexacao;
import br.leg.camara.indexacao.api.pesquisa.JobDePesquisa;
import br.leg.camara.indexacao.api.pesquisa.ServicoDePesquisa;
import br.leg.camara.indexacao.aplicacao.RepositorioDeJobsDeIndexacao;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;

import java.util.*;

import static java.util.stream.Collectors.toList;

@Slf4j
public class RepositorioDeJobsDoClasspath implements RepositorioDeJobsDeIndexacao {

	private final Configuracoes configuracoes;
	private final List<JobDeIndexacao> jobsConfigurados;

	public RepositorioDeJobsDoClasspath(@NonNull Configuracoes configuracoes, @NonNull ServicoDePesquisa servicoDePesquisa) {
		this.configuracoes = configuracoes;
		this.jobsConfigurados = carregarJobsConfigurados(servicoDePesquisa);
	}

	@Override
	public List<JobDeIndexacao> listarTodos() {
		return jobsConfigurados;
	}

	@Override
	public List<JobDeIndexacao> listarPorNomeDoIndice(String nomeDoIndice) {
		return jobsConfigurados.stream()
				.filter(job -> job.nomeDoIndice().equalsIgnoreCase(nomeDoIndice))
				.collect(toList());
	}

	@Override
	public JobDeIndexacao buscarPorNome(String nome) {
		return jobsConfigurados.stream()
				.filter(job -> job.nome().equalsIgnoreCase(nome))
				.findFirst()
				.orElseThrow(() -> new RuntimeException("job de indexação não encontrado com nome: " + nome));
	}

	private List<JobDeIndexacao> carregarJobsConfigurados(ServicoDePesquisa servicoDePesquisa) {
		List<JobDeIndexacao> jobs = new ArrayList<>();
		Set<String> nomesDosJobs = new HashSet<>();
		ServiceLoader<JobDeIndexacao> loader = ServiceLoader.load(JobDeIndexacao.class);
		loader.iterator().forEachRemaining( job -> {
			try {
				verificarNomeJobEhUnico(nomesDosJobs, job.nome());
				injetarDependenciasOpcionais(job, servicoDePesquisa);
				job.configurar(configuracoes);
				jobs.add(job);
			} catch (Exception e) {
				log.error(String.format("Erro ao configurar job de nome '%s' e classe %s: %s", job.nome(),
						job.getClass().getCanonicalName(), e.getMessage()), e);
			}
		});
		return jobs;
	}

	private void injetarDependenciasOpcionais(JobDeIndexacao job, ServicoDePesquisa servicoDePesquisa) {
		if (job instanceof JobDePesquisa) {
			((JobDePesquisa) job).setServicoDePesquisa(servicoDePesquisa);
		}
		if (job instanceof JobBuscadorDeJobs) {
			((JobBuscadorDeJobs) job).setBuscadorDeJobs(this);
		}
	}

	private void verificarNomeJobEhUnico(Set<String> nomesDosJobs, String nome) {
		if (nomesDosJobs.contains(nome)) {
			throw new IllegalArgumentException("Nome de job duplicado: " + nome);
		}
		nomesDosJobs.add(nome);
	}
}
