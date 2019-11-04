package br.leg.camara.indexacao.adaptadores.rest.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DadosJob {

	private String nome;
	private String nomeDoIndice;
}
