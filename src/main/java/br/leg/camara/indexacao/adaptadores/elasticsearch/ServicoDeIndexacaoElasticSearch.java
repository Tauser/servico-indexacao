package br.leg.camara.indexacao.adaptadores.elasticsearch;

import br.leg.camara.indexacao.api.pesquisa.Filtro;
import br.leg.camara.indexacao.api.pesquisa.ServicoDePesquisa;
import br.leg.camara.indexacao.aplicacao.ServicoDeIndexacao;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import io.searchbox.action.Action;
import io.searchbox.client.JestClient;
import io.searchbox.client.JestClientFactory;
import io.searchbox.client.JestResult;
import io.searchbox.client.config.HttpClientConfig;
import io.searchbox.core.*;
import io.searchbox.indices.CreateIndex;
import io.searchbox.indices.DeleteIndex;
import io.searchbox.indices.IndicesExists;
import io.searchbox.indices.aliases.GetAliases;
import io.searchbox.params.Parameters;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.util.*;

import static java.util.stream.Collectors.toList;

/**
 * {@link ServicoDeIndexacao} que indexa informações no ElasticSearch
 */
@Slf4j
public class ServicoDeIndexacaoElasticSearch implements ServicoDeIndexacao, ServicoDePesquisa {

	private static final String CAMPO_ID_DOCUMENTO = "id";
	private static final String CAMPO_DOCUMENTO_UPDATE = "doc";
	private static final String CAMPO_INDICATIVO_UPSERT = "doc_as_upsert";
	private static final String CAMINHO_CONFIGURACAO_INDICE = "elasticsearch/indices/%s.json";

	private final JestClient client;

	public ServicoDeIndexacaoElasticSearch(List<String> urlsServico) {
		if (urlsServico == null || urlsServico.isEmpty()) {
			throw new IllegalArgumentException("É necessário informar pelo menos uma url do ElasticSearch");
		}
		JestClientFactory factory = new JestClientFactory();
		factory.setHttpClientConfig(
				new HttpClientConfig.Builder(urlsServico)
						.multiThreaded(true).readTimeout(5_000).build()
		);
		client = factory.getObject();
		log.info("Urls do ElasticSearch: " + urlsServico);
	}

	@Override
	public void indexar(Collection<Map<String, ?>> documentos, String nomeDoIndice) {
		List<Update> operacoesDeIndexacao = documentos.stream()
				.map(doc -> criarOperacaoDeUpsert(doc, nomeDoIndice))
				.collect(toList());
		Bulk bulk = new Bulk.Builder()
				.defaultIndex(nomeDoIndice)
				.defaultType(nomeDoIndice)
				.addAction(operacoesDeIndexacao)
				.build();
		executarNoElasticSearch(bulk);
	}

	@Override
	public void removerDocumento(String nomeDoIndice, String idDocumento) {
		Delete operacao = new Delete.Builder(idDocumento).index(nomeDoIndice).type(nomeDoIndice).build();
		executarNoElasticSearch(operacao);
	}

	@Override
	public void removerDocumentos(@NonNull String nomeDoIndice, @NonNull List<String> idsDocumentos) {
		if (!idsDocumentos.isEmpty()) {
			String ids = String.join(",", idsDocumentos);
			String query = String.format("{\"query\": {\"terms\" : {\"id\" : [%s] }}}", ids);
			DeleteByQuery delete = new DeleteByQuery.Builder(query).addIndex(nomeDoIndice).build();
			executarNoElasticSearch(delete);
		}
	}


	@Override
	public List<String> listarIndices() {
		JestResult resultado = executarNoElasticSearch(new GetAliases.Builder().build());
		JsonObject json = resultado.getJsonObject();
		return json.keySet().stream()
				.filter(nome -> !nome.startsWith("."))
				.collect(toList());
	}

	@Override
	public void configurarIndiceSeAindaNaoCriado(String nomeDoIndice) {
		if (indiceExiste(nomeDoIndice)) {
			return;
		}
		String nomeDoArquivoDeConfiguracao = String.format(CAMINHO_CONFIGURACAO_INDICE, nomeDoIndice);
		String configuracoes = LeitorArquivo.lerDoClasspathComoString(nomeDoArquivoDeConfiguracao);
		if (configuracoes != null) {
			log.info("Índice {} não encontrado e configurações presentes no classpath. O índice será criado com essas configurações", nomeDoIndice);
			criarIndice(nomeDoIndice, configuracoes);
		}
	}

	@Override
	public void criarIndice(String nomeDoIndice, String configuracoes) {
		executarNoElasticSearch(new CreateIndex.Builder(nomeDoIndice)
				.settings(configuracoes)
				.build());
	}

	@Override
	public void removerIndice(String nomeDoIndice) {
		executarNoElasticSearch(new DeleteIndex.Builder(nomeDoIndice).build());
	}

	private Update criarOperacaoDeUpsert(Map<String, ?> documento, String nomeDoIndice) {
		if (!documento.containsKey(CAMPO_ID_DOCUMENTO)) {
			throw new IllegalArgumentException("Id do documento não encontrado! Campos: " + documento);
		}
		String idDocumento = documento.get(CAMPO_ID_DOCUMENTO).toString();

		Map<String, Object> payload = new HashMap<>();
		payload.put(CAMPO_DOCUMENTO_UPDATE, documento);
		payload.put(CAMPO_INDICATIVO_UPSERT, true);

		return new Update.Builder(payload).id(idDocumento).index(nomeDoIndice).type(nomeDoIndice).build();
	}

	private <T extends JestResult> T executarNoElasticSearch(Action<T> clientRequest) {
		try {
			T resultado = client.execute(clientRequest);
			if (!resultado.isSucceeded()) {
				log.error("Falha em excecução do ElasticSearch: {}. Json: {}", resultado.getErrorMessage(), resultado.getJsonObject());
				throw new RuntimeException("Falha ao executar comando no Elastic Search: " + resultado.getErrorMessage());
			}
			return resultado;
		} catch (IOException e) {
			throw new RuntimeException("Erro ao executar requisição no ElasticSearch", e);
		}
	}

	private boolean indiceExiste(String nomeDoIndice) {
		try {
			JestResult resultado = client.execute(new IndicesExists.Builder(nomeDoIndice).build());
			return resultado.isSucceeded();
		} catch (IOException e) {
			throw new RuntimeException("Erro ao verificar se índice existe no ElasticSearch", e);
		}
	}

	@Override
	public List<Map<String, ?>> pesquisarDocumentos(String nomeDoIndice, Filtro filtro) {	
		return pesquisarDocumentos(nomeDoIndice, null, filtro);
	}

	@Override
	public List<Map<String, ?>> pesquisarDocumentos(String nomeDoIndice, List<String> campos, Filtro filtro) {
		log.info("Iniciou");
		List<Map<String, ?>> lista = new ArrayList<>();		
		int size = 5000;
		String scroll = "2m";
		
		FiltroConversor conversor = new FiltroConversor(size);		
		String query = conversor.converter(campos, filtro);
		Search search = new Search.Builder(query)
				.addIndex(nomeDoIndice)
				.addType(nomeDoIndice)
				.setParameter(Parameters.SCROLL, scroll)
				.build();
		SearchResult resultado = executarNoElasticSearch(search);
		carregarListaComJestResult(lista, resultado);		
		String scrollId = resultado.getJsonObject().get("_scroll_id").getAsString();
		
		while(lista.size() < resultado.getTotal()) {			
			SearchScroll search2 = new SearchScroll.Builder(scrollId, scroll)
					.build();					
			JestResult resultado2 = executarNoElasticSearch(search2);
			carregarListaComJestResult(lista, resultado2);
			scrollId = resultado2.getJsonObject().get("_scroll_id").getAsString();
		}			
		log.info("Terminou");
		return lista;
	}

	@SuppressWarnings("unchecked")
	private void carregarListaComJestResult(List<Map<String, ?>> lista, JestResult resultado) {
		JsonObject base = resultado.getJsonObject().get("hits").getAsJsonObject();
		JsonArray hits = base.get("hits").getAsJsonArray();
		ObjectMapper mapper = new ObjectMapper();
				
		hits.forEach(sr -> {
			Map<String, ?> map = new HashMap<>();
			try {
				JsonObject source = sr.getAsJsonObject().get("_source").getAsJsonObject();
				map = mapper.readValue(source.toString(), map.getClass());
				lista.add(map);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			
		});
	}
}
