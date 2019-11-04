package br.leg.camara.indexacao.adaptadores.mongodb.agendamentos;

import br.leg.camara.indexacao.aplicacao.agendamento.Agendamento;
import br.leg.camara.indexacao.aplicacao.agendamento.RepositorioDeAgendamentos;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.index.Index;

import java.util.List;
import java.util.UUID;

import static br.leg.camara.indexacao.adaptadores.mongodb.agendamentos.CamposAgendamento.*;
import static org.springframework.data.mongodb.core.query.Criteria.where;
import static org.springframework.data.mongodb.core.query.Query.query;

@AllArgsConstructor
public class RepositorioDeAgendamentosMongo implements RepositorioDeAgendamentos {

	static final String NOME_COLECAO = "agendamentos";

	@NonNull
	private final MongoTemplate template;

	@Override
	public List<Agendamento> listarTodos() {
		return template.findAll(Agendamento.class, NOME_COLECAO);
	}

	@Override
	public void adicionar(Agendamento agendamento) {
		if (agendamento.getId() != null) {
			throw new IllegalArgumentException("Agendamento já possui um id!");
		}
		agendamento.setId(UUID.randomUUID().toString());
		template.insert(agendamento, NOME_COLECAO);
	}

	@Override
	public void remover(String idAgendamento) {
		template.remove(query(where(ID).is(idAgendamento)), NOME_COLECAO);
	}

	@Override
	public Agendamento buscarPorId(String idAgendamento) {
		return template.findById(idAgendamento, Agendamento.class, NOME_COLECAO);
	}

	public void criarIndicesDoBanco() {
		Index indiceNomeDoIndice = new Index().named(NOME_INDICE).background().on(NOME_INDICE, Sort.Direction.ASC);
		template.indexOps(NOME_COLECAO).ensureIndex(indiceNomeDoIndice);

		Index indiceNomeDoJob = new Index().named(NOME_JOB).background().on(NOME_JOB, Sort.Direction.ASC);
		template.indexOps(NOME_COLECAO).ensureIndex(indiceNomeDoJob);
	}
}
