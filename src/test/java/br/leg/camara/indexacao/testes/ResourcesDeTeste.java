package br.leg.camara.indexacao.testes;

import com.google.common.base.Joiner;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

import java.io.IOException;
import java.nio.file.Files;
import java.util.List;

import static java.nio.charset.StandardCharsets.UTF_8;

public abstract class ResourcesDeTeste {

	private static Resource comPath(String path) {
		return new ClassPathResource(path);
	}

	public static String lerDoClasspathComoString(String path) {
		return lerComoString(comPath(path), path);
	}

	private static String lerComoString(Resource resource, String path) {
		try {
			List<String> linhas = Files.readAllLines(resource.getFile().toPath(), UTF_8);
			return Joiner.on("\n").join(linhas);
		} catch (IOException e) {
			throw new RuntimeException(String.format("Erro ao tentar ler arquivo '%s' do classpath", path), e);
		}
	}
}
