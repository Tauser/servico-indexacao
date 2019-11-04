package br.leg.camara.indexacao.adaptadores.elasticsearch;

import org.junit.Test;

import static org.junit.Assert.*;

public class LeitorArquivoTest {

	@Test
	public void lerArquivoExistenteNoClasspath() {
		assertEquals("teste", LeitorArquivo.lerDoClasspathComoString("/teste-classpath.txt"));
	}

	@Test
	public void lerArquivoInexistenteNoClasspathRetornaNull() {
		assertNull(LeitorArquivo.lerDoClasspathComoString("/arquivo-inexistente.txt"));
	}

	@Test
	public void somenteArquivosDoClasspathDevemSerProcurados() {
		assertNull(LeitorArquivo.lerDoClasspathComoString("/../../../../../../../../../../../etc/fstab"));
	}
}