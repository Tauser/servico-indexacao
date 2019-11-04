package br.leg.camara.indexacao.adaptadores.mongodb.execucao;

interface CamposStatusExecucao {

	String ID = "_id";
	String NOME_JOB = "nomeJob";
	String PARAMETROS_EXECUCAO = "parametros";
	String TIMESTAMP_INICIO = "timestampInicio";
	String TIMESTAMP_TERMINO = "timestampTermino";
	String TOTAL_DE_DOCUMENTOS = "totalDeDocumentos";
	String DOCUMENTOS_LIDOS = "documentosLidos";
	String DOCUMENTOS_INDEXADOS = "documentosIndexados";
	String ERROS = "erros";
	String MENSAGEM = "mensagem";
}
