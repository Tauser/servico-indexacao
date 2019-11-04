package br.leg.camara.indexacao.adaptadores.mvc;

public class Urls {

	public static final String PAGINA_INICIAL = "/";	
	
	public static final String ACESSO_NEGADO = "/acessonegado";

	public static final String HISTORICO_EXECUCOES = "/execucoes";
	static final String EXECUTAR_JOB = "/execucoes/{nomeJob}";

	public static final String LISTAR_AGENDAMENTOS = "/agendamentos";
	public static final String NOVO_AGENDAMENTO = "/agendamentos/novo";
	static final String EXCLUIR_AGENDAMENTOS = "/agendamentos/{idAgendamento}/excluir";

	public static String excluirAgendamento(String idAgendamento) {
		return EXCLUIR_AGENDAMENTOS.replace("{idAgendamento}", idAgendamento);
	}

	public static final String LISTAR_INDICES = "/indices";
	public static final String NOVO_INDICE	 = "/indices/novo";
	static final String EXCLUIR_DOCUMENTO = "/indices/{nomeDoIndice}/documentos/excluir";
	static final String EXCLUIR_INDICE = "/indices/{nomeDoIndice}/excluir";

	public static String excluirIndice(String nomeDoIndice) {
		return EXCLUIR_INDICE.replace("{nomeDoIndice}", nomeDoIndice);
	}

	public static String excluirDocumento(String nomeDoIndice) {
		return EXCLUIR_DOCUMENTO.replace("{nomeDoIndice}", nomeDoIndice);
	}
}
