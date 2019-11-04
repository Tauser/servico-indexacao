package br.leg.camara.indexacao.aplicacao.execucao;

import br.leg.camara.indexacao.api.JobComposto;
import br.leg.camara.indexacao.api.JobDeIndexacao;
import br.leg.camara.indexacao.api.ParametrosExecucao;
import lombok.*;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static java.util.Collections.emptyList;
import static java.util.Objects.requireNonNull;

@EqualsAndHashCode(of = "id")
@Getter
@Builder
public class StatusExecucao {

	private String id;
	private final String nomeJob;
	private final transient List<String> nomesJobsFilhos;
	private final ParametrosExecucao parametrosExecucao;
	private final Instant timestampInicio;
	private Instant timestampTermino;
	@Setter
	private long totalDeDocumentos;
	private long documentosLidos;
	private long documentosIndexados;
	private final List<InformacoesExcecao> erros;
	private transient String mensagem;

	private StatusExecucao(String id, String nomeJob, List<String> nomesJobsFilhos, ParametrosExecucao parametros, Instant timestampInicio,
						   Instant timestampTermino, long totalDeDocumentos, long documentosLidos, long documentosIndexados,
						   List<InformacoesExcecao> erros, String mensagem) {
		this.id = id;
		this.nomeJob = requireNonNull(nomeJob);
		this.nomesJobsFilhos = nomesJobsFilhos == null ? emptyList() : new ArrayList<>(nomesJobsFilhos);
		this.parametrosExecucao = requireNonNull(parametros);
		this.timestampInicio = timestampInicio;
		this.timestampTermino = timestampTermino;
		this.totalDeDocumentos = totalDeDocumentos;
		this.documentosLidos = documentosLidos;
		this.documentosIndexados = documentosIndexados;
		this.erros = erros == null ? new ArrayList<>() : new ArrayList<>(erros);
		this.mensagem = mensagem;
	}

	static StatusExecucao iniciar(@NonNull String nomeJob, @NonNull ParametrosExecucao parametrosExecucao) {
		return iniciar(nomeJob, null, parametrosExecucao);
	}

	static StatusExecucao iniciar(@NonNull JobDeIndexacao job, @NonNull ParametrosExecucao parametrosExecucao) {
		List<String> nomesJobsFilhos = job instanceof JobComposto ? ((JobComposto) job).getNomesDosJobsComposicao() : null;
		return iniciar(job.nome(), nomesJobsFilhos, parametrosExecucao);
	}

	private static StatusExecucao iniciar(@NonNull String nomeJob, List<String> nomesJobsFilhos, @NonNull ParametrosExecucao parametrosExecucao) {
		return StatusExecucao.builder()
				.id(UUID.randomUUID().toString())
				.nomeJob(nomeJob)
				.nomesJobsFilhos(nomesJobsFilhos)
				.parametrosExecucao(parametrosExecucao)
				.timestampInicio(Instant.now())
				.build();
	}

	public boolean aindaEmExecucao() {
		return timestampTermino == null;
	}

	public List<InformacoesExcecao> getErros() {
		return Collections.unmodifiableList(erros);
	}

	void registrarExcecao(@NonNull Throwable excecao, @NonNull String mensagem) {
		this.erros.add(InformacoesExcecao.daExcecao(excecao));
		this.mensagem = mensagem;
	}

	void alterarMensagem(String mensagem) {
		this.mensagem = mensagem;
	}

	void encerrar() {
		timestampTermino = Instant.now();
	}

	long getDuracaoEmMs() {
		if (timestampTermino == null) {
			return Duration.between(timestampInicio, Instant.now()).toMillis();
		}
		return Duration.between(timestampInicio, timestampTermino).toMillis();
	}

	void incrementarDocumentosLidos() {
		documentosLidos++;
	}

	void incrementarDocumentosIndexados(int incremento) {
		documentosIndexados += incremento;
	}

	boolean possuiJob(JobDeIndexacao job) {
		if (nomeDeJobPresente(job.nome())) {
			return true;
		}
		if (job instanceof JobComposto) {
			JobComposto jobComposto = (JobComposto) job;
			for (String nomeSubJob : jobComposto.getNomesDosJobsComposicao()) {
				if (nomeDeJobPresente(nomeSubJob)) {
					return true;
				}
			}
		}
		return false;
	}

	private boolean nomeDeJobPresente(String nomeJob) {
		return nomeJob.equals(this.nomeJob) || nomesJobsFilhos.contains(nomeJob);
	}
}
