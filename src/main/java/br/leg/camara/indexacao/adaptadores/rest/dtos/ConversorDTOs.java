package br.leg.camara.indexacao.adaptadores.rest.dtos;

import br.leg.camara.indexacao.api.JobDeIndexacao;
import br.leg.camara.indexacao.api.ParametrosExecucao;
import br.leg.camara.indexacao.aplicacao.agendamento.Agendamento;
import br.leg.camara.indexacao.aplicacao.util.TimeUtils;
import org.modelmapper.Converter;
import org.modelmapper.ModelMapper;
import org.modelmapper.spi.MappingContext;

import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.util.stream.Collectors.toList;

/**
 * Responsável por converter classes do modelo para DTOs usados pela API REST e
 * vice-versa. A ideia é não expor diretamente o modelo para clientes externos.
 */
public class ConversorDTOs {

	private final ModelMapper mapper;

	public ConversorDTOs() {
		mapper = new ModelMapper();
		mapper.addConverter(new ParametrosConverter());
		mapper.addConverter(new InstantConverter());
	}

	public <T> List<T> converterLista(List<?> lista, Class<T> classeAlvo) {
		return lista.stream()
				.map(item -> converter(item, classeAlvo))
				.collect(toList());
	}

	@SuppressWarnings("unchecked")
	public <T> T converter(Object objeto, Class<T> classeAlvo) {
		//caso especial em que não foi possível ajustar no modelMapper (aparentemente, não funciona para mapear interface -> bean)
		if (classeAlvo.equals(DadosJob.class) && objeto instanceof JobDeIndexacao) {
			JobDeIndexacao job = (JobDeIndexacao) objeto;
			return (T) new DadosJob(job.nome(), job.nomeDoIndice());
		}

		return mapper.map(objeto, classeAlvo);
	}

	public Agendamento paraAgendamento(DadosAgendamento dados) {
		if (dados == null) {
			return null;
		}
		return Agendamento.builder()
				.id(dados.getId())
				.nomeDoJob(dados.getNomeDoJob())
				.nomeDoIndice(dados.getNomeDoIndice())
				.parametros(converterMapaParaParametros(dados.getParametros()))
				.expressaoCron(dados.getExpressaoCron())
				.build();
	}

	private static ParametrosExecucao converterMapaParaParametros(Map<String,String[]> mapa) {
		ParametrosExecucao params = ParametrosExecucao.vazio();
		if (mapa != null ) {
			mapa.forEach(params::adicionarValorMultiplo);
		}
		return params;
	}


	private static class ParametrosConverter implements Converter<ParametrosExecucao, Map<String, String[]>> {
		@Override
		public Map<String, String[]> convert(MappingContext<ParametrosExecucao, Map<String, String[]>> context) {
			ParametrosExecucao params = context.getSource();
			Map<String, String[]> retorno = new HashMap<>();
			if (params != null) {
				params.forEach(retorno::put);
			}
			return retorno;
		}
	}

	private static class InstantConverter implements Converter<Instant, String> {
		@Override
		public String convert(MappingContext<Instant, String> context) {
			return TimeUtils.formatarTimestamp(context.getSource());
		}
	}
}
