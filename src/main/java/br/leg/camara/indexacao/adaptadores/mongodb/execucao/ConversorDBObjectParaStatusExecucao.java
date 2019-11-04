package br.leg.camara.indexacao.adaptadores.mongodb.execucao;

import br.leg.camara.indexacao.aplicacao.execucao.InformacoesExcecao;
import br.leg.camara.indexacao.aplicacao.execucao.StatusExecucao;
import com.mongodb.BasicDBList;
import com.mongodb.DBObject;
import org.bson.types.BasicBSONList;
import org.springframework.core.convert.converter.Converter;

import java.util.Date;
import java.util.List;

import static br.leg.camara.indexacao.adaptadores.mongodb.ConversaoUtils.converterParametros;
import static br.leg.camara.indexacao.adaptadores.mongodb.ConversaoUtils.dateParaInstant;
import static br.leg.camara.indexacao.adaptadores.mongodb.execucao.CamposInformacoesErro.*;
import static br.leg.camara.indexacao.adaptadores.mongodb.execucao.CamposStatusExecucao.*;
import static java.util.stream.Collectors.toList;

public class ConversorDBObjectParaStatusExecucao implements Converter<DBObject, StatusExecucao> {

	@Override
	public StatusExecucao convert(DBObject json) {
		return StatusExecucao.builder()
				.id((String) json.get(ID))
				.nomeJob((String) json.get(NOME_JOB))
				.parametrosExecucao(converterParametros((DBObject) json.get(PARAMETROS_EXECUCAO)))
				.timestampInicio(dateParaInstant((Date) json.get(TIMESTAMP_INICIO)))
				.timestampTermino(dateParaInstant((Date) json.get(TIMESTAMP_TERMINO)))
				.totalDeDocumentos(paraLong((Number) json.get(TOTAL_DE_DOCUMENTOS)))
				.documentosLidos(paraLong((Number) json.get(DOCUMENTOS_LIDOS)))
				.documentosIndexados(paraLong((Number) json.get(DOCUMENTOS_INDEXADOS)))
				.mensagem((String) json.get(MENSAGEM))
				.erros(extrairErros((BasicDBList) json.get(ERROS)))
				.build();
	}

	private static long paraLong(Number number) {
		return number == null ? 0 : number.longValue();
	}

	private List<InformacoesExcecao> extrairErros(BasicBSONList listaJson) {
		return listaJson.stream()
				.map(itemLista -> {
					DBObject json = (DBObject) itemLista;
					return InformacoesExcecao.builder()
							.classeDaExcecao((String) json.get(CLASSE_EXCECAO))
							.mensagemErro((String) json.get(MENSAGEM_ERRO))
							.stackTrace((String) json.get(STACK_TRACE))
						.build();
				}).collect(toList());
	}
}
