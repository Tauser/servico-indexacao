package br.leg.camara.indexacao.adaptadores.rest.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.List;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DadosExecucao {

	private String id;
	private String nomeJob;
	private Map<String, String[]> parametrosExecucao;
	private String timestampInicio;
	private String timestampTermino;
	private long totalDeDocumentos;
	private long documentosLidos;
	private long documentosIndexados;
	private List<DadosExcecao> erros;
	private String mensagem;
}
