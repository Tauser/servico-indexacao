package br.leg.camara.indexacao.config;

import org.keycloak.adapters.KeycloakConfigResolver;
import org.keycloak.adapters.springboot.KeycloakSpringBootConfigResolver;
import org.keycloak.adapters.springsecurity.KeycloakConfiguration;
import org.keycloak.adapters.springsecurity.config.KeycloakWebSecurityConfigurerAdapter;
import org.keycloak.adapters.springsecurity.filter.KeycloakCsrfRequestMatcher;
import org.springframework.context.annotation.Bean;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.session.SessionRegistryImpl;
import org.springframework.security.web.authentication.session.RegisterSessionAuthenticationStrategy;
import org.springframework.security.web.authentication.session.SessionAuthenticationStrategy;

import javax.servlet.http.HttpServletRequest;

@KeycloakConfiguration
public class ConfiguracaoKeycloak extends KeycloakWebSecurityConfigurerAdapter {

	private static final String PREFIXO_URLS_API = "/api/";

	@Bean
	public KeycloakConfigResolver KeycloakConfigResolver() {
		return new KeycloakSpringBootConfigResolver();
	}

	@Override
	protected SessionAuthenticationStrategy sessionAuthenticationStrategy() {
		return new RegisterSessionAuthenticationStrategy(new SessionRegistryImpl());
	}

	@Bean
	public AuthenticationProvider authenticationProvider() {
		return keycloakAuthenticationProvider();
	}

	@Override
	protected KeycloakCsrfRequestMatcher keycloakCsrfRequestMatcher() {
		//desabilita CSRF para urls da api
		return new KeycloakCsrfRequestMatcher() {
			@Override
			public boolean matches(HttpServletRequest request) {
				String uri = request.getRequestURI();
				if (!request.getContextPath().equals("/")) {
					uri = uri.replaceFirst(request.getContextPath(), "");
				}
				if (uri.startsWith(PREFIXO_URLS_API)) {
					return false;
				}
				return super.matches(request);
			}
		};
	}

	@Override
	protected void configure(HttpSecurity http) throws Exception {
		super.configure(http);
		http
			.headers()
				.frameOptions().sameOrigin()
			.and()
			.authorizeRequests()
				.antMatchers("/css/**", "/webjars/**", PREFIXO_URLS_API + "**").permitAll()
				.anyRequest().authenticated()
			.and()
			//padrão do Keycloak usa / como logoutSucessUrl, o que torna a página pública, por isso mudamos para outra qualquer
				.logout().logoutSuccessUrl("http://www.camara.leg.br");
	}
}
