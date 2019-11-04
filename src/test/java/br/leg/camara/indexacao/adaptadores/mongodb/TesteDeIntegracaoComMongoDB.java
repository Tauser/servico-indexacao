package br.leg.camara.indexacao.adaptadores.mongodb;

import br.leg.camara.indexacao.config.ConfiguracaoRepositorios;
import com.github.fakemongo.Fongo;
import com.mongodb.Mongo;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.config.AbstractMongoConfiguration;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.convert.CustomConversions;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@ContextConfiguration(classes = TesteDeIntegracaoComMongoDB.ConfigMongo.class)
public abstract class TesteDeIntegracaoComMongoDB {

	@Autowired
	protected MongoTemplate mongoTemplate;

	protected void removerTodosDocumentosDaColecao(String nomeColecao) {
		mongoTemplate.remove(new Query(), nomeColecao);
	}

	protected long quantidadeDeDocumentosDaColecao(String nomeColecao) {
		return mongoTemplate.count(new Query(), nomeColecao);
	}

	@Configuration
	static class ConfigMongo extends AbstractMongoConfiguration {

		@Override
		protected String getDatabaseName() {
			return "mongo-test";
		}

		@Override
		public Mongo mongo() {
			return new Fongo("mongo-test").getMongo();
		}

		@Override
		public CustomConversions customConversions() {
			return ConfiguracaoRepositorios.criarConversores();
		}
	}
}
