package br.leg.camara.indexacao.adaptadores.mongodb.agendamentos;

interface CamposAgendamento {

	String ID = "_id";
	String NOME_JOB = "nomeDoJob";
	String NOME_INDICE = "nomeDoIndice";
	String EXPRESSAO_CRON = "expressaoCron";
	String PARAMETROS = "parametros";
}
