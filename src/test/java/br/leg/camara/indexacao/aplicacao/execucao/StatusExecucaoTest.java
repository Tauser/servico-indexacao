package br.leg.camara.indexacao.aplicacao.execucao;

import br.leg.camara.indexacao.api.JobComposto;
import br.leg.camara.indexacao.api.JobDeIndexacao;
import br.leg.camara.indexacao.api.ParametrosExecucao;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class StatusExecucaoTest {

	@Test
	public void deveIniciarComIdGeradoEInicioNaoNulo() {
		StatusExecucao status = StatusExecucao.iniciar("teste", ParametrosExecucao.vazio());

		assertNotNull(status.getId());
		assertNotNull(status.getTimestampInicio());
	}

	@Test
	public void deveIndicarCorretamenteJobsSimplesQuePossui() {
		JobDeIndexacao jobPresente = criarMockJobSimples("teste");
		JobDeIndexacao jobAusente = criarMockJobSimples("ausente");

		StatusExecucao statusExecucao = StatusExecucao.iniciar(jobPresente, ParametrosExecucao.vazio());

		assertTrue(statusExecucao.possuiJob(jobPresente));
		assertFalse(statusExecucao.possuiJob(jobAusente));
	}

	@Test
	public void deveIndicarCorretamenteJobsCompostoQuePossui() {
		JobDeIndexacao jobComposto = criarMockJobComposto("composto", Arrays.asList("job-1", "job-2"));
		JobDeIndexacao job1 = criarMockJobSimples("job-1");
		JobDeIndexacao job3 = criarMockJobSimples("job-3");

		StatusExecucao statusExecucao = StatusExecucao.iniciar(jobComposto, ParametrosExecucao.vazio());

		assertTrue(statusExecucao.possuiJob(jobComposto));
		assertTrue(statusExecucao.possuiJob(job1));
		assertFalse(statusExecucao.possuiJob(job3));
	}

	@Test
	public void deveIndicarCorretamenteJobSimplesRodandoDentroDeComposto() {
		JobDeIndexacao jobComposto = criarMockJobComposto("job-composto", Arrays.asList("job-1", "job-2"));
		JobDeIndexacao job1 = criarMockJobSimples("job-1");

		StatusExecucao statusExecucao = StatusExecucao.iniciar(job1, ParametrosExecucao.vazio());

		assertTrue(statusExecucao.possuiJob(jobComposto));
	}

	private JobDeIndexacao criarMockJobSimples(String nomeJob) {
		JobDeIndexacao job = mock(JobDeIndexacao.class);
		when(job.nome()).thenReturn(nomeJob);
		return job;
	}

	private JobDeIndexacao criarMockJobComposto(String nomeJob, List<String> jobsComposicao) {
		JobComposto job = mock(JobComposto.class);
		when(job.nome()).thenReturn(nomeJob);
		when(job.getNomesDosJobsComposicao()).thenReturn(jobsComposicao);
		return job;
	}
}