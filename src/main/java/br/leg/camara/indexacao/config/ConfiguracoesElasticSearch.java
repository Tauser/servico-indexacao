package br.leg.camara.indexacao.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@ConfigurationProperties("elasticsearch")
@Data
public class ConfiguracoesElasticSearch {

	List<String> urls;
}
