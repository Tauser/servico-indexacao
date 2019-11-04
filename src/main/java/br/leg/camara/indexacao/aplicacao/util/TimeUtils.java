package br.leg.camara.indexacao.aplicacao.util;

import java.time.*;
import java.time.format.DateTimeFormatter;

public abstract class TimeUtils {

	private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
	private static final ZoneId FUSO_DF = ZoneId.of("America/Sao_Paulo");

	public static String formatarDuracao(Duration duracao) {
		long segundos = duracao.getSeconds();
		if (segundos < 60) {
			return String.format("%ds", segundos);
		}

		long minutos = duracao.toMinutes();
		segundos = duracao.minusMinutes(minutos).getSeconds();
		if (minutos < 60) {
			return String.format("%dm %ds", minutos, segundos);
		}

		long horas = duracao.toHours();
		minutos = duracao.minusHours(horas).toMinutes();
		segundos = duracao.minusMinutes((60 * horas) + minutos).getSeconds();
		return String.format("%dh %dm %ds", horas, minutos, segundos);
	}

	public static String formatarTimestamp(Instant instant) {
		if (instant == null) {
			return null;
		}
		LocalDateTime noFusoDoDF = LocalDateTime.ofInstant(instant, FUSO_DF);
		return FORMATTER.format(noFusoDoDF);
	}
}
