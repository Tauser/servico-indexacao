package br.leg.camara.indexacao.adaptadores.elasticsearch;

import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ClassPathResource;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

import static java.util.stream.Collectors.joining;

@Slf4j
class LeitorArquivo {

	static String lerDoClasspathComoString(String path) {
		try {
			ClassPathResource resource = new ClassPathResource(path);
			if (!resource.exists()) {
				return null;
			}
			InputStream inputStream = resource.getInputStream();
			
			String resultado;

			try (BufferedReader reader = new BufferedReader(
					new InputStreamReader(inputStream, StandardCharsets.UTF_8))) {
				resultado = reader.lines()
						.collect(joining("\n"));
			}

			log.debug("Resultado da leitura do arquivo do classpath: {}", resultado);
			return resultado;
		} catch (IOException e) {
			throw new RuntimeException(String.format("Erro ao tentar ler recurso %s do classpath", path), e);
		}
	}
}
