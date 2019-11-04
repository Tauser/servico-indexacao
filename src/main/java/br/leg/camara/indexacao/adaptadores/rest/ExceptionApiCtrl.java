package br.leg.camara.indexacao.adaptadores.rest;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * Tratamento genérico de exceções da API Rest
 */
@RestControllerAdvice
@Slf4j
public class ExceptionApiCtrl {

	@ExceptionHandler(value = { IllegalArgumentException.class, HttpMediaTypeNotSupportedException.class,
			HttpRequestMethodNotSupportedException.class, HttpMessageNotReadableException.class})
	public ResponseEntity<RespostaErroApi> tratarErrosDoCliente(Exception ex) {
		String mensagemErro = ex.getMessage();
		if (ex instanceof HttpMessageNotReadableException) {
			mensagemErro = "Formato do corpo da requisição inválido. Verifique os dados enviados";
		}
		return new ResponseEntity<>(new RespostaErroApi(mensagemErro), HttpStatus.BAD_REQUEST);
	}

	@ExceptionHandler(value = { Exception.class})
	public ResponseEntity<RespostaErroApi> tratarOutrasExcecoes(Exception ex) {
		log.error("Erro não tratado na API REST", ex);
		return new ResponseEntity<>(
				new RespostaErroApi("Ocorreu um erro interno. Tente novamente ou entre em contato com a DITEC"),
				HttpStatus.INTERNAL_SERVER_ERROR);
	}

	@Data
	@AllArgsConstructor
	private static class RespostaErroApi {
		private String mensagem;
	}
}
