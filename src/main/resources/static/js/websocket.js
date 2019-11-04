let stompClient = null;
let domStatusConexao = null;
let formatter = new Intl.NumberFormat('pt-BR');

function setConnected(connected) {
	domStatusConexao.html(connected ? 'Conectado' : 'Desconectado');
}

function connect(url) {
	let socket = new SockJS(url);
	stompClient = Stomp.over(socket);
	stompClient.connect({}, function(frame) {
		setConnected(true);
		console.log('websocket conectado: ' + frame);
		stompClient.subscribe('/topicos/status', function(response) {
			atualizarInformacoes(JSON.parse(response.body));
		});
	}, function() {
		console.log('websocket desconectado');
		setConnected(false);
	});
}

function executarJob(nomeDoJob) {
	stompClient.send("/app/executarJob", {}, JSON.stringify({
		'nomeDoJob' : nomeDoJob
	}));
}

function percentual(numero, total) {
	if (numero === 0 || total === 0)
		return 0;
	return Math.floor((numero / total) * 100);
}

function atualizarInformacoes(status) {
	if (status.documentosIndexados !== status.totalDeDocumentos) {
		atualizarProgresso(status.nomeJob, 
				formatter.format(status.documentosIndexados) + ' documentos indexados de um total de ' + formatter.format(status.totalDeDocumentos), 
				percentual(status.documentosIndexados, status.totalDeDocumentos));
	} else {
		if (status.documentosIndexados > 0) {
			finalizar(status.nomeJob, status.mensagem);
		}
	}
}

function criarCachesDom() {
	domStatusConexao = $('#status-conexao');
}

function iniciar(nomeDoJob) {
	let jobEl = $('#' + nomeDoJob);

	jobEl.find('button').toggleClass('d-none');
	jobEl.find('.progresso-job').toggleClass('d-none');

	atualizarProgresso(nomeDoJob, 'Iniciando...', 0);
}

function atualizarProgresso(nomeDoJob, mensagem, percentual) {
	let jobEl = $('#' + nomeDoJob + ' .status-job');
	let jobPr = $('#' + nomeDoJob + ' .progresso-job .progress-bar');

	jobEl.find('.mensagem').html(mensagem);

	jobPr.attr('aria-valuenow', percentual);
	jobPr.css('width', percentual + '%');
	jobPr.text(percentual + '%');

    $('#' + nomeDoJob).find('button').addClass('d-none');
    $('#' + nomeDoJob).find('.progresso-job').removeClass('d-none');
}

function finalizar(nomeDoJob, mensagem) {
	let jobEl = $('#' + nomeDoJob);

	jobEl.find('button').toggleClass('d-none');
	jobEl.find('.progresso-job').toggleClass('d-none');

	atualizarProgresso(nomeDoJob, mensagem, 100);
}
