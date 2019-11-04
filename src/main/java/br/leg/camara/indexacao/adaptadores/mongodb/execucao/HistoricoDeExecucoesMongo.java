package br.leg.camara.indexacao.adaptadores.mongodb.execucao;

import br.leg.camara.indexacao.aplicacao.execucao.HistoricoDeExecucoes;
import br.leg.camara.indexacao.aplicacao.execucao.StatusExecucao;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.index.Index;
import org.springframework.data.mongodb.core.query.Query;

import java.time.*;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.List;

import static br.leg.camara.indexacao.adaptadores.mongodb.execucao.CamposStatusExecucao.NOME_JOB;
import static br.leg.camara.indexacao.adaptadores.mongodb.execucao.CamposStatusExecucao.TIMESTAMP_INICIO;
import static br.leg.camara.indexacao.adaptadores.mongodb.execucao.CamposStatusExecucao.TIMESTAMP_TERMINO;
import static org.springframework.data.mongodb.core.query.Criteria.where;

@AllArgsConstructor
public class HistoricoDeExecucoesMongo implements HistoricoDeExecucoes {

	private static final int LIMITE_QTD_ULTIMAS_EXECUCOES = 100;
	private static final ZoneId FUSO_HORARIO_DF = ZoneId.of("America/Sao_Paulo");
	static final String NOME_COLECAO = "historicoExecucoes";

	@NonNull
	private final MongoTemplate template;

	@Override
	public void adicionar(StatusExecucao status) {
		template.insert(status, NOME_COLECAO);
	}

	@Override
	public List<StatusExecucao> listarExecucoesDoDia(LocalDate dataLocal) {
		ZonedDateTime zeroHoraDaData = ZonedDateTime.of(dataLocal, LocalTime.MIDNIGHT, FUSO_HORARIO_DF);
		Instant comecoDoDia = Instant.from(zeroHoraDaData);
		Instant fimDoDia = comecoDoDia.plus(1, ChronoUnit.DAYS);

		Query query = new Query();
		query.addCriteria(where(TIMESTAMP_INICIO).gte(new Date(comecoDoDia.toEpochMilli())).lt(new Date(fimDoDia.toEpochMilli())));
		query.with(new Sort(Sort.Direction.DESC, TIMESTAMP_INICIO));

		return template.find(query, StatusExecucao.class, NOME_COLECAO);
	}

	@Override
	public List<StatusExecucao> listarUltimasDoJob(String nomeDoJob) {
		Query query = new Query();
		query.addCriteria(where(NOME_JOB).is(nomeDoJob));
		query.with(new Sort(Sort.Direction.DESC, TIMESTAMP_INICIO));
		query.limit(LIMITE_QTD_ULTIMAS_EXECUCOES);
		return template.find(query, StatusExecucao.class, NOME_COLECAO);
	}

	public void criarIndicesDoBanco() {
		criarIndiceEm(TIMESTAMP_INICIO);
		criarIndiceEm(TIMESTAMP_TERMINO);
		criarIndiceEm(NOME_COLECAO);
	}

	private void criarIndiceEm(String nomeDoCampo) {
		Index indice = new Index().named(nomeDoCampo).background().on(nomeDoCampo, Sort.Direction.ASC);
		template.indexOps(NOME_COLECAO).ensureIndex(indice);
	}
}
