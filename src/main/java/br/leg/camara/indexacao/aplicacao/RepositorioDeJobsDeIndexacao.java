package br.leg.camara.indexacao.aplicacao;

import br.leg.camara.indexacao.api.BuscadorDeJobs;
import br.leg.camara.indexacao.api.JobDeIndexacao;

import java.util.List;

public interface RepositorioDeJobsDeIndexacao extends BuscadorDeJobs {

	List<JobDeIndexacao> listarTodos();

	List<JobDeIndexacao> listarPorNomeDoIndice(String nomeDoIndice);

}
