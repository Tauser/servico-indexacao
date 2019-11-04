package br.leg.camara.indexacao.adaptadores.rest.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class DadosAgendamento {

	private String id;
	private String expressaoCron;
	private String nomeDoIndice;
	private String nomeDoJob;
	private Map<String, String[]> parametros;
}
