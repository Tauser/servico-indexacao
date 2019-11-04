package br.leg.camara.indexacao.adaptadores.mongodb.execucao;

import br.leg.camara.indexacao.aplicacao.execucao.InformacoesExcecao;
import br.leg.camara.indexacao.aplicacao.execucao.StatusExecucao;
import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import org.springframework.core.convert.converter.Converter;

import java.util.List;

import static br.leg.camara.indexacao.adaptadores.mongodb.ConversaoUtils.converterParametros;
import static br.leg.camara.indexacao.adaptadores.mongodb.ConversaoUtils.instantParaDate;
import static br.leg.camara.indexacao.adaptadores.mongodb.execucao.CamposInformacoesErro.*;
import static br.leg.camara.indexacao.adaptadores.mongodb.execucao.CamposStatusExecucao.*;

public class ConversorStatusExecucaoParaDBObject implements Converter<StatusExecucao, DBObject> {

	@Override
	public DBObject convert(StatusExecucao status) {
		final BasicDBObject json = new BasicDBObject();
		json.put(ID, status.getId());
		json.put(NOME_JOB, status.getNomeJob());
		json.put(PARAMETROS_EXECUCAO, converterParametros(status.getParametrosExecucao()));
		json.put(TIMESTAMP_INICIO, instantParaDate(status.getTimestampInicio()));
		json.put(TIMESTAMP_TERMINO, instantParaDate(status.getTimestampTermino()));
		json.put(TOTAL_DE_DOCUMENTOS, status.getTotalDeDocumentos());
		json.put(DOCUMENTOS_LIDOS, status.getDocumentosLidos());
		json.put(DOCUMENTOS_INDEXADOS, status.getDocumentosIndexados());
		json.put(MENSAGEM, status.getMensagem());
		json.put(ERROS, converterErros(status.getErros()));
		return json;
	}

	private DBObject converterErros(List<InformacoesExcecao> erros) {
		final BasicDBList lista = new BasicDBList();

		for (InformacoesExcecao erro : erros) {
			DBObject json = new BasicDBObject();
			json.put(CLASSE_EXCECAO, erro.getClasseDaExcecao());
			json.put(MENSAGEM_ERRO, erro.getMensagemErro());
			json.put(STACK_TRACE, erro.getStackTrace());

			lista.add(json);
		}
		return lista;
	}
}
