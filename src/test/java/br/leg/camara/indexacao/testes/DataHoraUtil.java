package br.leg.camara.indexacao.testes;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.Locale;

class DataHoraUtil {

	private static final SimpleDateFormat FORMATTER = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss", new Locale("pt", "BR"));

	static Instant parse(String s) {
		try {
			return FORMATTER.parse(s).toInstant();
		} catch (ParseException e) {
			throw new RuntimeException("Erro no parse", e);
		}
	}
}
