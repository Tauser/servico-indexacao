package br.leg.camara.indexacao.aplicacao.execucao;

import br.leg.camara.indexacao.api.JobDeIndexacao;
import br.leg.camara.indexacao.api.ParametrosExecucao;
import br.leg.camara.indexacao.aplicacao.RepositorioDeJobsDeIndexacao;
import br.leg.camara.indexacao.aplicacao.ServicoDeIndexacao;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;

import java.io.Closeable;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

import static java.util.Comparator.comparing;
import static java.util.stream.Collectors.toList;

@Slf4j
@AllArgsConstructor
public class ServicoDeExecucaoDeIndexacoes implements Closeable {

	@NonNull
	private final RepositorioDeJobsDeIndexacao repositorioDeJobs;
	@NonNull
	private final HistoricoDeExecucoes historicoDeExecucoes;
	@NonNull
	private final ServicoDeIndexacao servicoDeIndexacao;
	@NonNull
	private final ExecutorService executorService;
	private final List<StatusExecucao> jobsEmExecucao = new CopyOnWriteArrayList<>();

	public void executarTodosDoIndice(String nomeDoIndice, ParametrosExecucao parametros) {
		List<JobDeIndexacao> jobs = repositorioDeJobs.listarPorNomeDoIndice(nomeDoIndice);
		if (jobs.isEmpty()) {
			throw new IllegalArgumentException("Nenhum job encontrado com nome de índice " + nomeDoIndice);
		}

		log.info("Disparando {} jobs do índice {} ({}) com parâmetros: {}", jobs.size(), nomeDoIndice,
				jobs.stream().map(JobDeIndexacao::nome).collect(toList()), parametros);
		for (JobDeIndexacao job : jobs) {
			executar(job.nome(), (status) -> {}, parametros);
		}
		log.info("Todos jobs do índice {} disparados", nomeDoIndice);
	}

	public void executar(@NonNull String nomeDoJob) {
		executar(nomeDoJob, (status) -> {}, ParametrosExecucao.vazio());
	}

	public void executar(@NonNull String nomeDoJob, @NonNull Consumer<StatusExecucao> listener, @NonNull ParametrosExecucao parametros) {
		Runnable tarefa = () -> {
			JobDeIndexacao job = repositorioDeJobs.buscarPorNome(nomeDoJob);

			StatusExecucao status = StatusExecucao.iniciar(job, parametros);

			NotificadorExecucaoJob notificador = new NotificadorExecucaoJob(status, listener);
			notificador.info("Iniciando job de indexação %s com parâmetros %s", nomeDoJob, parametros);

			long quantidadeDeDocumentos;
			Exception excecaoOcorrida = null;
			try {
				garantirExecucaoDeJobNaoThreadSafe(job, status);

				servicoDeIndexacao.configurarIndiceSeAindaNaoCriado(job.nomeDoIndice());
				
				job.iniciar(parametros);

				//joga valor em variável para evitar repetir consultas pesadas no job
				quantidadeDeDocumentos = job.quantidadeDeDocumentos();
				status.setTotalDeDocumentos(quantidadeDeDocumentos);
				notificador.info("%s documentos a indexar", quantidadeDeDocumentos);

				int tamanhoDoLote = job.quantidadeDeDocumentosPorLote();
				if (tamanhoDoLote <= 0) {
					throw new IllegalArgumentException("Tamanho de lote inválido: " + tamanhoDoLote);
				}
				List<Map<String, ?>> loteParaIndexar = new ArrayList<>(tamanhoDoLote);
				for (int i = 0; i < quantidadeDeDocumentos; i++) {
					loteParaIndexar.add(job.proximoDocumento());
					status.incrementarDocumentosLidos();

					if (loteParaIndexar.size() == tamanhoDoLote || i == quantidadeDeDocumentos - 1) {
						notificador.debug("Indexando %d documentos - %s", loteParaIndexar.size(), job.nomeDoIndice());
						servicoDeIndexacao.indexar(loteParaIndexar, job.nomeDoIndice());
						status.incrementarDocumentosIndexados(loteParaIndexar.size());
						notificador.notificarListener();
						loteParaIndexar.clear();
					}
				}
			} catch (Exception erro) {
				notificador.erro(erro,"Erro durante execução do job %s: %s", job.nome(), erro.getMessage());
				excecaoOcorrida = erro;
			} finally {
				notificador.info("Encerrando job %s", nomeDoJob);
				if (!(excecaoOcorrida instanceof ConcorrenciaNaoPermitidaException)) {
					//previne encerramento de jobs que não chegaram a ser iniciados
					try {
						job.encerrar();
					} catch (Exception e) {
						log.error("Erro ao encerrar job " + job.nome(), e);
					}
				}

				notificarJobSobreSucessoOuErro(job, excecaoOcorrida);
				status.encerrar();
				jobsEmExecucao.remove(status);
				historicoDeExecucoes.adicionar(status);
			}

			notificador.infoTerminoJob();
		};

		executorService.execute(tarefa);
	}

	private synchronized void garantirExecucaoDeJobNaoThreadSafe(JobDeIndexacao job, StatusExecucao statusJob) {
		if (!job.threadSafe()) {
			for (StatusExecucao emExecucao : jobsEmExecucao) {
				if (emExecucao.possuiJob(job)) {
					throw new ConcorrenciaNaoPermitidaException(job.nome());
				}
			}
		}
		jobsEmExecucao.add(statusJob);
	}

	private void notificarJobSobreSucessoOuErro(JobDeIndexacao job, Exception excecaoOcorrida) {
		try {
			if (excecaoOcorrida != null) {
				job.onErro(excecaoOcorrida);
			} else {
				job.onSucesso();
			}
		} catch (Exception erro) {
			String operacao = excecaoOcorrida == null ? "sucesso" : "erro";
			log.error(String.format("Erro ao notificar job sobre %s", operacao), excecaoOcorrida);
		}
	}

	public List<JobDeIndexacao> listarJobsDisponiveis() {
		return repositorioDeJobs.listarTodos();
	}

	public List<StatusExecucao> listarExecucoesDoDia(LocalDate data) {
		List<StatusExecucao> execucoesDoDia = new ArrayList<>();
		if (data.isEqual(LocalDate.now())) {
			execucoesDoDia.addAll(jobsEmExecucao);
		}
		execucoesDoDia.addAll(historicoDeExecucoes.listarExecucoesDoDia(data));
		execucoesDoDia.sort(comparing(StatusExecucao::getTimestampInicio).reversed());
		return execucoesDoDia;
	}

	public List<StatusExecucao> listarUltimasExecucoesDoJob(String nomeDoJob) {
		return historicoDeExecucoes.listarUltimasDoJob(nomeDoJob);
	}

	@Override
	public void close() {
		log.info("Encerrando Thread-Pool de execução de jobs");
		executorService.shutdown();
		try {
			if (!executorService.awaitTermination(1, TimeUnit.SECONDS)) {
				executorService.shutdownNow();
			}
		} catch (InterruptedException e) {
			executorService.shutdownNow();
		}
		log.info("Thread-Pool de execução de jobs encerrado");
	}
}
