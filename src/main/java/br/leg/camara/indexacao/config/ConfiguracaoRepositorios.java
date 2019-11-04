package br.leg.camara.indexacao.config;

import br.leg.camara.indexacao.adaptadores.classloader.RepositorioDeJobsDoClasspath;
import br.leg.camara.indexacao.adaptadores.mongodb.agendamentos.ConversorAgendamentoParaDBObject;
import br.leg.camara.indexacao.adaptadores.mongodb.agendamentos.ConversorDBObjectParaAgendamento;
import br.leg.camara.indexacao.adaptadores.mongodb.agendamentos.RepositorioDeAgendamentosMongo;
import br.leg.camara.indexacao.adaptadores.mongodb.execucao.ConversorDBObjectParaStatusExecucao;
import br.leg.camara.indexacao.adaptadores.mongodb.execucao.ConversorStatusExecucaoParaDBObject;
import br.leg.camara.indexacao.adaptadores.mongodb.execucao.HistoricoDeExecucoesMongo;
import br.leg.camara.indexacao.adaptadores.spring.ConfiguracoesSpring;
import br.leg.camara.indexacao.api.Configuracoes;
import br.leg.camara.indexacao.api.pesquisa.ServicoDePesquisa;
import com.mongodb.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.data.mongodb.config.AbstractMongoConfiguration;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.convert.CustomConversions;

import java.util.Arrays;

import static java.util.Collections.singletonList;

@Configuration
public class ConfiguracaoRepositorios extends AbstractMongoConfiguration {

	@Autowired
	private Environment environment;

	@Value("${spring.data.mongodb.username}")
	private String username;
	@Value("${spring.data.mongodb.password}")
	private char[] password;
	@Value("${spring.data.mongodb.database}")
	private String database;
	@Value("${spring.data.mongodb.host}")
	private String host;
	@Value("${spring.data.mongodb.port}")
	private int port;

	@Bean
	public RepositorioDeJobsDoClasspath repositorioDeJobsDoClasspath(ServicoDePesquisa servicoDePesquisa) {
		Configuracoes configuracoes = new ConfiguracoesSpring(environment);
		return new RepositorioDeJobsDoClasspath(configuracoes, servicoDePesquisa);
	}

	@Bean
	public RepositorioDeAgendamentosMongo repositorioDeAgendamentos(MongoTemplate mongoTemplate) {
		return new RepositorioDeAgendamentosMongo(mongoTemplate);
	}

	@Bean
	public HistoricoDeExecucoesMongo historicoDeExecucoes(MongoTemplate mongoTemplate) {
		return new HistoricoDeExecucoesMongo(mongoTemplate);
	}

	@Bean
	CommandLineRunner criarIndicesSeAusentes(RepositorioDeAgendamentosMongo repositorioDeAgendamentos, HistoricoDeExecucoesMongo historicoDeExecucoes) {
		return args -> {
			repositorioDeAgendamentos.criarIndicesDoBanco();
			historicoDeExecucoes.criarIndicesDoBanco();
		};
	}

	public static CustomConversions criarConversores() {
		return new CustomConversions(Arrays.asList(
				new ConversorAgendamentoParaDBObject(),
				new ConversorDBObjectParaAgendamento(),

				new ConversorStatusExecucaoParaDBObject(),
				new ConversorDBObjectParaStatusExecucao()
		));
	}

	@Override
	public CustomConversions customConversions() {
		return criarConversores();
	}

	@Override
	protected String getDatabaseName() {
		return database;
	}

	@Override
	public Mongo mongo() {
		ServerAddress address = new ServerAddress(host, port);
		MongoCredential credenciais = MongoCredential.createCredential(username, getDatabaseName(), password);
		MongoClientOptions options = MongoClientOptions.builder()
				.connectTimeout(3000)
				.socketTimeout(3000)
				.serverSelectionTimeout(3000)
				.build();
		return new MongoClient(address, singletonList(credenciais), options);
	}
}
