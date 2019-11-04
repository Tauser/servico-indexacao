package br.leg.camara.indexacao.config;

import br.leg.camara.indexacao.adaptadores.elasticsearch.ServicoDeIndexacaoElasticSearch;
import br.leg.camara.indexacao.adaptadores.spring.agendamento.ServicoDeAgendamentosSpring;
import br.leg.camara.indexacao.aplicacao.RepositorioDeJobsDeIndexacao;
import br.leg.camara.indexacao.aplicacao.ServicoDeIndexacao;
import br.leg.camara.indexacao.aplicacao.agendamento.RepositorioDeAgendamentos;
import br.leg.camara.indexacao.aplicacao.agendamento.ServicoDeAgendamentos;
import br.leg.camara.indexacao.aplicacao.execucao.HistoricoDeExecucoes;
import br.leg.camara.indexacao.aplicacao.execucao.ServicoDeExecucaoDeIndexacoes;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Slf4j
@Configuration
@EnableScheduling
public class ConfiguracaoServicos {

	@Bean
	public ServicoDeIndexacaoElasticSearch servicoDeIndexacao(ConfiguracoesElasticSearch configs) {
		return new ServicoDeIndexacaoElasticSearch(configs.getUrls());
	}

	@Bean
	public ServicoDeExecucaoDeIndexacoes servicoDeExecucaoDeIndexacoes(RepositorioDeJobsDeIndexacao repositorioDeJobs,
																	   HistoricoDeExecucoes historicoDeExecucoes,
																	   ServicoDeIndexacao servicoDeIndexacao) {
		int cpus = Runtime.getRuntime().availableProcessors();
		//não usa número de CPUs pois é necessário deixar threads livres para dispatcher web (REST/MVC/WebSocket)
		//e agendador do Spring (jobs agendados)
		int numeroThreads = Math.max(1, cpus / 2);
		log.info("Criando thread-pool para execução de indexações com {} threads", numeroThreads);
		ExecutorService executorService = Executors.newFixedThreadPool(numeroThreads);

		return new ServicoDeExecucaoDeIndexacoes(repositorioDeJobs, historicoDeExecucoes, servicoDeIndexacao, executorService);
	}

	@Bean
	public ServicoDeAgendamentos servicoDeAgendamentos(RepositorioDeAgendamentos repositorioDeAgendamentos,
													   TaskScheduler taskScheduler,
													   ServicoDeExecucaoDeIndexacoes servicoDeExecucaoDeIndexacoes) {
		return new ServicoDeAgendamentosSpring(repositorioDeAgendamentos, servicoDeExecucaoDeIndexacoes, taskScheduler);
	}
}
