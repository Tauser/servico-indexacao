package br.leg.camara.indexacao.adaptadores.classloader;

import br.leg.camara.indexacao.api.Configuracoes;
import br.leg.camara.indexacao.api.JobDeIndexacao;
import br.leg.camara.indexacao.api.ParametrosExecucao;

import java.util.Map;

public class JobDeIndexacaoOutroMock implements JobDeIndexacao {

	@Override
	public void configurar(Configuracoes configuracoes) {
	}

	@Override
	public String nome() {
		return "outro job de teste";
	}

	@Override
	public String nomeDoIndice() {
		return "teste";
	}

	@Override
	public void iniciar(ParametrosExecucao parametros) {
	}

	@Override
	public long quantidadeDeDocumentos() {
		return 0;
	}

	@Override
	public Map<String, String> proximoDocumento() {
		return null;
	}

	@Override
	public void encerrar() {
	}
}
