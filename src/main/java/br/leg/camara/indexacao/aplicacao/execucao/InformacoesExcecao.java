package br.leg.camara.indexacao.aplicacao.execucao;

import lombok.*;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
@Getter
@EqualsAndHashCode
@ToString
public class InformacoesExcecao {

	private final String classeDaExcecao;
	private final String mensagemErro;
	private final String stackTrace;

	static InformacoesExcecao daExcecao(@NonNull Throwable excecao) {
		try (StringWriter writer = new StringWriter();
			 PrintWriter printWriter = new PrintWriter(writer)) {
			excecao.printStackTrace(printWriter);

			return InformacoesExcecao.builder()
					.classeDaExcecao(excecao.getClass().getCanonicalName())
					.mensagemErro(excecao.getMessage())
					.stackTrace(writer.toString())
					.build();
		} catch (IOException e) {
			throw new RuntimeException("Erro ao extrair stack trace da exceção informada", e);
		}
	}
}



