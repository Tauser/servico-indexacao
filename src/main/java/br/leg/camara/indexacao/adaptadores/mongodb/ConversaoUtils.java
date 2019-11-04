package br.leg.camara.indexacao.adaptadores.mongodb;

import br.leg.camara.indexacao.api.ParametrosExecucao;
import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;

import java.time.Instant;
import java.util.Collection;
import java.util.Date;

/**
 * Utilitários usados na conversão de/para Bson/Java
 */
public class ConversaoUtils {

	public static Date instantParaDate(Instant instant) {
		return instant == null ? null : new Date(instant.toEpochMilli());
	}

	public static Instant dateParaInstant(Date date) {
		return date == null ? null : Instant.ofEpochMilli(date.getTime());
	}

	public static DBObject converterParametros(ParametrosExecucao parametros) {
		final BasicDBObject json = new BasicDBObject();
		parametros.forEach(json::put);
		return json;
	}

	@SuppressWarnings("unchecked")
	public static ParametrosExecucao converterParametros(DBObject json) {
		ParametrosExecucao params = ParametrosExecucao.vazio();
		json.keySet().forEach(nome -> params.adicionarValorMultiplo(nome, (Collection<String>) json.get(nome)));
		return params;
	}

}
