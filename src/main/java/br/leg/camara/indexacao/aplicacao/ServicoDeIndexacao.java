package br.leg.camara.indexacao.aplicacao;

import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * Responsável por indexar documentos e gerenciar índices. Abstrai a implementação da ferramenta de indexação usada.
 */
public interface ServicoDeIndexacao {

	void indexar(Collection<Map<String, ?>> documentos, String nomeDoIndice);

	void removerDocumento(String nomeDoIndice, String idDocumento);

	List<String> listarIndices();

	void configurarIndiceSeAindaNaoCriado(String nomeDoIndice);

	void criarIndice(String nomeDoIndice, String configuracoes);

	void removerIndice(String nomeDoIndice);
	
}
