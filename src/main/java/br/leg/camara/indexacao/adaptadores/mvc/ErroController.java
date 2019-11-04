package br.leg.camara.indexacao.adaptadores.mvc;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class ErroController {

	@GetMapping(value = Urls.ACESSO_NEGADO)
	public String acessoNegado() {
		return "acesso-negado";
	}
}
