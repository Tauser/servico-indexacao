package br.leg.camara.indexacao.aplicacao.agendamento;

import java.util.List;

public interface ServicoDeAgendamentos {

	List<Agendamento> listarTodos();

	void adicionar(Agendamento agendamento);

	void remover(String idAgendamento);

	Agendamento buscarPorId(String idAgendamento);
}
