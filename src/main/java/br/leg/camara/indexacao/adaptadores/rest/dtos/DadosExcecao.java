package br.leg.camara.indexacao.adaptadores.rest.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
class DadosExcecao {

	private String classe;
	private String mensagemErro;
	private String stackTrace;
}



