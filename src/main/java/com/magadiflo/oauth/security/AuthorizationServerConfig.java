package com.magadiflo.oauth.security;

import java.util.Arrays;
import java.util.Base64;

import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.token.TokenEnhancerChain;
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;
import org.springframework.security.oauth2.provider.token.store.JwtTokenStore;

import com.magadiflo.oauth.services.IUsuarioService;

@RefreshScope
@Configuration
@EnableAuthorizationServer
public class AuthorizationServerConfig extends AuthorizationServerConfigurerAdapter {
	
	private final Environment env;
	private final PasswordEncoder passwordEncoder;
	private final AuthenticationManager authenticationManager;
	private final InfoAdicionalToken infoAdicionalToken;
	private final IUsuarioService usuarioService;
	
	public AuthorizationServerConfig(Environment env, PasswordEncoder passwordEncoder, AuthenticationManager authenticationManager, 
			InfoAdicionalToken infoAdicionalToken, IUsuarioService usuarioService) {
		this.env = env;
		this.passwordEncoder = passwordEncoder;
		this.authenticationManager = authenticationManager;
		this.infoAdicionalToken = infoAdicionalToken;
		this.usuarioService = usuarioService;
	}

	/**
	 * Estos dos endpoints del m??todo de abajo, est?? protegido por autenticaci??n via Http Basic
	 * usando las credenciales del cliente:
	 * Header Authorization Basic: client id: client secret
	 * */
	@Override
	public void configure(AuthorizationServerSecurityConfigurer security) throws Exception {
		security.tokenKeyAccess("permitAll()")//tokenKeyAccess, es el endpoint para generar el token, para autenticarnos con la ruta POST: /oauth/token
			.checkTokenAccess("isAuthenticated()"); //isAuthenticated(), M??todo de springSecurity que valida que el cliente est?? autentcado
	}

	@Override
	public void configure(ClientDetailsServiceConfigurer clients) throws Exception {
		clients.inMemory()
			.withClient(this.env.getProperty("config.security.oauth.client.id"))
			.secret(this.passwordEncoder.encode(this.env.getProperty("config.security.oauth.client.secret")))
			.scopes("read", "write")
			.authorizedGrantTypes("password", "refresh_token")
			.accessTokenValiditySeconds(3600) //1h
			.refreshTokenValiditySeconds(3600); //1h
	}

	/**
	 * Este m??todo que recibe un authorizationServerEndPointsConfigurer
	 * est?? relacionado al endpoint de OAuth2 del servidor de autorizaci??n que se encarga de generar
	 * el token. 
	 * El endpoint ser?? del tipoi [POST] y su path: /oauth/token
	 * */
	@Override
	public void configure(AuthorizationServerEndpointsConfigurer endpoints) throws Exception {
		TokenEnhancerChain tokenEnhancerChain = new TokenEnhancerChain();
		tokenEnhancerChain.setTokenEnhancers(Arrays.asList(this.infoAdicionalToken, this.accessTokenConverter()));
		
		endpoints.authenticationManager(this.authenticationManager)
			.tokenStore(this.tokenStore())
			.accessTokenConverter(this.accessTokenConverter())
			.tokenEnhancer(tokenEnhancerChain)
			.userDetailsService(this.usuarioService);
	}

	@Bean
	public JwtTokenStore tokenStore() {
		return new JwtTokenStore(this.accessTokenConverter());
	}

	@Bean
	public JwtAccessTokenConverter accessTokenConverter() {
		JwtAccessTokenConverter tokenConverter = new JwtAccessTokenConverter();
		tokenConverter.setSigningKey(Base64.getEncoder().encodeToString(this.env.getProperty("config.security.oauth.jwt.key").getBytes()));
		return tokenConverter;
	}

}
