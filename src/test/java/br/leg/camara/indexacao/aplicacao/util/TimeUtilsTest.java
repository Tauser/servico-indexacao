package br.leg.camara.indexacao.aplicacao.util;

import org.junit.Test;

import java.time.Duration;

import static org.junit.Assert.*;

public class TimeUtilsTest {

	@Test
	public void formatarDuracaoMenosDe60Segundos() {
		assertEquals("45s", TimeUtils.formatarDuracao(Duration.ofSeconds(45)));
	}

	@Test
	public void formatarDuracaoMaisDe1Minuto() {
		assertEquals("1m 10s", TimeUtils.formatarDuracao(Duration.ofSeconds(70)));
	}

	@Test
	public void formatarDuracaoMaisDe1Hora() {
		assertEquals("1h 20m 10s", TimeUtils.formatarDuracao(Duration.ofMinutes(80).plusSeconds(10)));
	}
}