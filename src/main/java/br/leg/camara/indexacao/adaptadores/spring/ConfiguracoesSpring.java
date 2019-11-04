package br.leg.camara.indexacao.adaptadores.spring;

import br.leg.camara.indexacao.api.Configuracoes;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import org.springframework.core.env.Environment;

@AllArgsConstructor
public class ConfiguracoesSpring implements Configuracoes {

	@NonNull
	private final Environment environment;

	@Override
	public String getPropriedadeObrigatoria(String nome) {
		String valor = environment.getProperty(nome);
		if (valor == null) {
			throw new RuntimeException("Propriedade obrigatória não encontrada: " + nome);
		}
		return valor;
	}

	@Override
	public String getPropriedade(String nome, String valorDefault) {
		return environment.getProperty(nome, valorDefault);
	}
}
