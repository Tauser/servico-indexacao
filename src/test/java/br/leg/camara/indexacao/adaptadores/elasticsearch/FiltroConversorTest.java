package br.leg.camara.indexacao.adaptadores.elasticsearch;

import static org.junit.Assert.assertEquals;

import java.util.Arrays;
import java.util.List;

import org.junit.Test;

import br.leg.camara.indexacao.api.pesquisa.Filtro;

public class FiltroConversorTest {

	private static final int REGISTROS_POR_CONSULTA = 5000;
	private List<String> campos = Arrays.asList("id","nome","telefone");
	private Filtro filtro = Filtro.builder()
			 .e("nome").igual("Josefino da Silva")
			 .ou("telefone").diferente("61998891235").build(); 
	
	@Test
	public void campoEFiltroPreenchido() {								
		FiltroConversor conversor = new FiltroConversor(REGISTROS_POR_CONSULTA);
		String query = conversor.converter(campos, filtro);
				
		assertEquals("{  \"size\":5000,  \"_source\": [\"id\",\"nome\",\"telefone\"]  ,\"query\": {     \"query_string\": {       \"query\": \"nome:Josefino da Silva OR (NOT telefone:61998891235)\"    }  }}", query);
	}

	@Test
	public void somenteCampoPreenchido() {				
		FiltroConversor conversor = new FiltroConversor(REGISTROS_POR_CONSULTA);
		String query = conversor.converter(campos, Filtro.vazio());
				
		assertEquals("{  \"size\":5000,  \"_source\": [\"id\",\"nome\",\"telefone\"]}", query);
	}

	@Test
	public void somenteFiltroPreenchido() {	
		FiltroConversor conversor = new FiltroConversor(REGISTROS_POR_CONSULTA);
		String query = conversor.converter(null, filtro);
				
		assertEquals("{  \"size\":5000  ,\"query\": {     \"query_string\": {       \"query\": \"nome:Josefino da Silva OR (NOT telefone:61998891235)\"    }  }}", query);
	}

	@Test
	public void filtroNulo() {		
		FiltroConversor conversor = new FiltroConversor(REGISTROS_POR_CONSULTA);
		String query = conversor.converter(null, null);
				
		assertEquals("{  \"size\":5000}", query);
	}

	@Test
	public void filtroVazio() {
		FiltroConversor conversor = new FiltroConversor(REGISTROS_POR_CONSULTA);
		String query = conversor.converter(null, Filtro.vazio());
				
		assertEquals("{  \"size\":5000}",  query);
	}
}
