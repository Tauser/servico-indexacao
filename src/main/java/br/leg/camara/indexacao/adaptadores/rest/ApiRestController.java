package br.leg.camara.indexacao.adaptadores.rest;

import br.leg.camara.indexacao.adaptadores.rest.dtos.ConversorDTOs;
import br.leg.camara.indexacao.adaptadores.rest.dtos.DadosAgendamento;
import br.leg.camara.indexacao.adaptadores.rest.dtos.DadosExecucao;
import br.leg.camara.indexacao.adaptadores.rest.dtos.DadosJob;
import br.leg.camara.indexacao.api.JobDeIndexacao;
import br.leg.camara.indexacao.api.ParametrosExecucao;
import br.leg.camara.indexacao.aplicacao.ServicoDeIndexacao;
import br.leg.camara.indexacao.aplicacao.agendamento.Agendamento;
import br.leg.camara.indexacao.aplicacao.agendamento.ServicoDeAgendamentos;
import br.leg.camara.indexacao.aplicacao.execucao.ServicoDeExecucaoDeIndexacoes;
import br.leg.camara.indexacao.aplicacao.execucao.StatusExecucao;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.servlet.http.HttpServletRequest;
import java.net.URI;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static java.util.Objects.requireNonNull;

@RestController
public class ApiRestController {

	private static final DateTimeFormatter FORMATTER_DIA = DateTimeFormatter.ofPattern("dd-MM-yyyy");

	private final ServicoDeExecucaoDeIndexacoes servicoExecucoes;
	private final ServicoDeAgendamentos servicoAgendamentos;
	private final ServicoDeIndexacao servicoDeIndexacao;
	private final ConversorDTOs conversorDTOs;

	public ApiRestController(ServicoDeExecucaoDeIndexacoes servicoExecucoes, ServicoDeAgendamentos servicoAgendamentos,
							 ServicoDeIndexacao servicoDeIndexacao) {
		this.servicoExecucoes = requireNonNull(servicoExecucoes);
		this.servicoAgendamentos = requireNonNull(servicoAgendamentos);
		this.servicoDeIndexacao = requireNonNull(servicoDeIndexacao);
		this.conversorDTOs = new ConversorDTOs();
	}

	@GetMapping("/api/jobs")
	public List<DadosJob> listarJobsDisponiveis() {
		List<JobDeIndexacao> jobs = servicoExecucoes.listarJobsDisponiveis();
		return conversorDTOs.converterLista(jobs, DadosJob.class);
	}

	@PostMapping("/api/execucoes/por-indice/{nomeDoIndice}")
	@ResponseStatus(HttpStatus.ACCEPTED)
	public void executarJobsDoIndice(@PathVariable("nomeDoIndice") String nomeDoIndice, HttpServletRequest request) {
		ParametrosExecucao parametros = ParametrosExecucao.comValores(request.getParameterMap());
		servicoExecucoes.executarTodosDoIndice(nomeDoIndice, parametros);
	}

	@GetMapping({"/api/execucoes/por-dia/{data}", "/api/execucoes"})
	public List<DadosExecucao> listarHistoricoExecucoesDoDia(@PathVariable(name = "data", required = false) String data) {
		LocalDate diaPesquisado = (data == null) ? LocalDate.now(): LocalDate.parse(data, FORMATTER_DIA);
		List<StatusExecucao> execucoes = servicoExecucoes.listarExecucoesDoDia(diaPesquisado);
		return conversorDTOs.converterLista(execucoes, DadosExecucao.class);
	}

	@GetMapping("/api/execucoes/por-job/{nomeDoJob}")
	public List<DadosExecucao> listarUltimasExecucoesDoJob(@PathVariable("nomeDoJob") String nomeDoJob) {
		List<StatusExecucao> ultimas = servicoExecucoes.listarUltimasExecucoesDoJob(nomeDoJob);
		return conversorDTOs.converterLista(ultimas, DadosExecucao.class);
	}

	@PostMapping("/api/execucoes/por-job/{nomeDoJob}")
	@ResponseStatus(HttpStatus.ACCEPTED)
	public void executarJobComNome(@PathVariable("nomeDoJob") String nomeDoJob, HttpServletRequest request) {
		ParametrosExecucao parametros = ParametrosExecucao.comValores(request.getParameterMap());
		servicoExecucoes.executar(nomeDoJob, status -> {}, parametros);
	}

	@GetMapping("/api/agendamentos")
	public List<DadosAgendamento> listarAgendamentos() {
		List<Agendamento> agendamentos = servicoAgendamentos.listarTodos();
		return conversorDTOs.converterLista(agendamentos, DadosAgendamento.class);
	}

	@GetMapping("/api/agendamentos/{id}")
	public ResponseEntity<DadosAgendamento> buscarAgendamentoPorId(@PathVariable("id") String id) {
		Agendamento agendamento = servicoAgendamentos.buscarPorId(id);
		if (agendamento == null) {
			return ResponseEntity.notFound().build();
		}
		return ResponseEntity.ok(conversorDTOs.converter(agendamento, DadosAgendamento.class));
	}

	@PostMapping("/api/agendamentos")
	@ResponseStatus(HttpStatus.CREATED)
	public ResponseEntity<String> cadastrarAgendamento(@RequestBody DadosAgendamento dados) {
		Agendamento agendamento = conversorDTOs.paraAgendamento(dados);
		servicoAgendamentos.adicionar(agendamento);
		String id = agendamento.getId();
		URI uriCriada = ServletUriComponentsBuilder
				.fromCurrentRequest()
				.path("/{id}")
				.buildAndExpand(id)
				.toUri();
		return ResponseEntity.created(uriCriada).body(id);
	}

	@DeleteMapping("/api/agendamentos/{id}")
	public void excluirAgendamento(@PathVariable("id") String id) {
		servicoAgendamentos.remover(id);
	}

	@GetMapping("/api/indices")
	public List<String> listarIndicesExistentes() {
		return servicoDeIndexacao.listarIndices();
	}

	@PutMapping("/api/indices/{nomeDoIndice}")
	public void criarNovoIndice(@PathVariable("nomeDoIndice") String nomeDoIndice, @RequestBody String configuracoes) {
		servicoDeIndexacao.criarIndice(nomeDoIndice, configuracoes);
	}

	@DeleteMapping("/api/indices/{nomeDoIndice}")
	public void removerIndice(@PathVariable("nomeDoIndice") String nomeDoIndice) {
		servicoDeIndexacao.removerIndice(nomeDoIndice);
	}

	@DeleteMapping("/api/indices/{nomeDoIndice}/documentos/{idDocumento}")
	public void removerDocumento(@PathVariable("nomeDoIndice") String nomeDoIndice,
								 @PathVariable("idDocumento") String idDocumento) {
		servicoDeIndexacao.removerDocumento(nomeDoIndice, idDocumento);
	}
}
