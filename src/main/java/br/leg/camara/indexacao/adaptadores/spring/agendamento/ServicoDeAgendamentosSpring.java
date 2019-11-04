package br.leg.camara.indexacao.adaptadores.spring.agendamento;

import br.leg.camara.indexacao.aplicacao.agendamento.Agendamento;
import br.leg.camara.indexacao.aplicacao.agendamento.RepositorioDeAgendamentos;
import br.leg.camara.indexacao.aplicacao.agendamento.ServicoDeAgendamentos;
import br.leg.camara.indexacao.aplicacao.execucao.ServicoDeExecucaoDeIndexacoes;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.support.CronTrigger;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ScheduledFuture;

import static java.util.Objects.requireNonNull;

@Slf4j
public class ServicoDeAgendamentosSpring implements ServicoDeAgendamentos {

	private final RepositorioDeAgendamentos repositorio;
	private final ServicoDeExecucaoDeIndexacoes servicoDeExecucaoDeIndexacoes;
	private final TaskScheduler taskScheduler;
	private final Map<String, ScheduledFuture<?>> tarefasAgendadas;

	public ServicoDeAgendamentosSpring(RepositorioDeAgendamentos repositorio,
									   ServicoDeExecucaoDeIndexacoes servicoDeExecucaoDeIndexacoes,
									   TaskScheduler taskScheduler) {
		this.repositorio = requireNonNull(repositorio);
		this.servicoDeExecucaoDeIndexacoes = requireNonNull(servicoDeExecucaoDeIndexacoes);
		this.taskScheduler = requireNonNull(taskScheduler);
		this.tarefasAgendadas = new HashMap<>();
		agendarItensPersistidos();
	}

	private void agendarItensPersistidos() {
		List<Agendamento> agendamentos = repositorio.listarTodos();
		log.info("{} agendamentos encontrados", agendamentos.size());
		for (Agendamento agendamento : agendamentos) {
			log.info("Agendamento encontrado: {}", agendamento);
			agendar(agendamento);
		}
	}

	@Override
	public List<Agendamento> listarTodos() {
		return repositorio.listarTodos();
	}

	@Override
	public void adicionar(Agendamento agendamento) {
		repositorio.adicionar(agendamento);
		agendar(agendamento);
		log.info("Agendamento adicionado: {}", agendamento);
	}

	private void agendar(Agendamento agendamento) {
		try {
			CronTrigger trigger = new CronTrigger(agendamento.getExpressaoCron());
			JobRunner task = new JobRunner(agendamento, servicoDeExecucaoDeIndexacoes);
			ScheduledFuture<?> future = taskScheduler.schedule(task, trigger);
			tarefasAgendadas.put(agendamento.getId(), future);
		} catch (java.lang.IllegalArgumentException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void remover(String idAgendamento) {
		repositorio.remover(idAgendamento);
		log.info("Agendamento removido: {}", idAgendamento);
		ScheduledFuture<?> tarefa = tarefasAgendadas.remove(idAgendamento);
		if (tarefa != null) {
			boolean tarefaCancelada = tarefa.cancel(true);
			if (!tarefaCancelada) {
				log.warn("Tarefa do agendamento de id {} não pode ser cancelada", idAgendamento);
			}
		}
	}

	@Override
	public Agendamento buscarPorId(String idAgendamento) {
		return repositorio.buscarPorId(idAgendamento);
	}

	@AllArgsConstructor
	private static class JobRunner implements Runnable {
		@NonNull
		private final Agendamento agendamento;
		@NonNull
		private final ServicoDeExecucaoDeIndexacoes servico;

		@Override
		public void run() {
			if (agendamento.deUmUnicoJob()) {
				log.info("Iniciando execução agendada do job com nome {}", agendamento.getNomeDoJob());
				servico.executar(agendamento.getNomeDoJob(), statusExecucao -> {}, agendamento.getParametros());
			} else {
				log.info("Iniciando execução agendada dos jobs que manipulam o índice {}", agendamento.getNomeDoIndice());
				servico.executarTodosDoIndice(agendamento.getNomeDoIndice(), agendamento.getParametros());
			}
		}
	}
}
