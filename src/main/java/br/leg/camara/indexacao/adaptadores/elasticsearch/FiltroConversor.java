package br.leg.camara.indexacao.adaptadores.elasticsearch;

import java.util.List;
import java.util.stream.Collectors;

import br.leg.camara.indexacao.api.pesquisa.Condicao;
import br.leg.camara.indexacao.api.pesquisa.Filtro;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class FiltroConversor {	
	private final int size;
	
	public String converter(List<String> campos, Filtro filtro) {
		String query = "{";
		query += "  \"size\":" + size;
		if(campos != null && campos.size() > 0) {
			query += ",  \"_source\": [" + String.join(",", campos.stream().map(x -> "\"" + x + "\"").collect(Collectors.toList())) + "]";
		}
		
		if(filtro != null && filtro.getCondicoes().size() != 0) {
			query += "  ,\"query\": {";
			query += "     \"query_string\": {";
			query += "       \"query\": \"";
			
			for (int i = 0; i < filtro.getCondicoes().size(); i++) {
				Condicao condicao = filtro.getCondicoes().get(i);
				if(i != 0) {
					switch (condicao.getAgrupador()) {
						case E:
							query += " AND ";						
							break;
						case OU:
							query += " OR ";
							break;
					}
				}
				
				switch (condicao.getOperacao()) {
				case igual:
					query += condicao.getAtributo() + ":" +condicao.getValor();	
					break;
				case diferente:
					query += "(NOT " + condicao.getAtributo() + ":" +condicao.getValor() + ")";	
					break;
				case maior:
					query += condicao.getAtributo() + ":>" +condicao.getValor();	
					break;
				case menor:
					query += condicao.getAtributo() + ":<" +condicao.getValor();	
					break;
				case maiorIgual:
					query += condicao.getAtributo() + ":>=" +condicao.getValor();	
					break;
				case menorIgual:
					query += condicao.getAtributo() + ":<=" +condicao.getValor();	
					break;
				}
				
			}
			query += "\"";
			query += "    }";
			query += "  }";
			
		}
		query += "}";
		return query.trim();
	}
}
