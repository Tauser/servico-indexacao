package br.leg.camara.indexacao.adaptadores.mongodb.agendamentos;

import br.leg.camara.indexacao.aplicacao.agendamento.Agendamento;
import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import org.springframework.core.convert.converter.Converter;

import static br.leg.camara.indexacao.adaptadores.mongodb.ConversaoUtils.converterParametros;
import static br.leg.camara.indexacao.adaptadores.mongodb.agendamentos.CamposAgendamento.*;

public class ConversorAgendamentoParaDBObject implements Converter<Agendamento, DBObject> {

	@Override
	public DBObject convert(Agendamento agendamento) {
		final BasicDBObject json = new BasicDBObject();
		json.put(ID, agendamento.getId());
		json.put(EXPRESSAO_CRON, agendamento.getExpressaoCron());
		json.put(NOME_INDICE, agendamento.getNomeDoIndice());
		json.put(NOME_JOB, agendamento.getNomeDoJob());
		json.put(PARAMETROS, converterParametros(agendamento.getParametros()));
		return json;
	}
}
