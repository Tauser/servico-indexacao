package br.leg.camara.indexacao.adaptadores.mongodb.agendamentos;

import br.leg.camara.indexacao.aplicacao.agendamento.Agendamento;
import com.mongodb.DBObject;
import org.springframework.core.convert.converter.Converter;

import static br.leg.camara.indexacao.adaptadores.mongodb.ConversaoUtils.converterParametros;
import static br.leg.camara.indexacao.adaptadores.mongodb.agendamentos.CamposAgendamento.*;

public class ConversorDBObjectParaAgendamento implements Converter<DBObject, Agendamento> {

	@Override
	public Agendamento convert(DBObject json) {
		return Agendamento.builder()
				.id((String) json.get(ID))
				.nomeDoJob((String) json.get(NOME_JOB))
				.nomeDoIndice((String) json.get(NOME_INDICE))
				.expressaoCron((String) json.get(EXPRESSAO_CRON))
				.parametros(converterParametros((DBObject) json.get(PARAMETROS)))
				.build();
	}
}
