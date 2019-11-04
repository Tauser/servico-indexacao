package br.leg.camara.indexacao.adaptadores.classloader;

import br.leg.camara.indexacao.api.ConfiguracoesMapa;
import br.leg.camara.indexacao.api.JobDeIndexacao;
import br.leg.camara.indexacao.api.pesquisa.ServicoDePesquisa;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;

public class RepositorioDeJobsDoClasspathTest {

	private RepositorioDeJobsDoClasspath repositorio;

	public RepositorioDeJobsDoClasspathTest() {
		repositorio = new RepositorioDeJobsDoClasspath(new ConfiguracoesMapa(), mock(ServicoDePesquisa.class));
	}

	@Test
	public void listarTodos() {
		List<JobDeIndexacao> jobs = repositorio.listarTodos();
		assertTrue(jobs.size() >= 2);
	}

	@Test
	public void buscarPorNome() {
		JobDeIndexacao job = repositorio.buscarPorNome("job de teste");
		assertNotNull(job);
	}

	@Test
	public void listarPorNomeDoIndice() {
		List<JobDeIndexacao> jobs = repositorio.listarPorNomeDoIndice("teste");
		assertEquals(2, jobs.size());

		for (JobDeIndexacao job : jobs) {
			assertEquals("Nome de índice diferente: " + job.nomeDoIndice(), "teste", job.nomeDoIndice());
		}
	}
}