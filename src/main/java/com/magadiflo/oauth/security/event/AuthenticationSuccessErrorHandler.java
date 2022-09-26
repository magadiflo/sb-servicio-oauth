package com.magadiflo.oauth.security.event;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AuthenticationEventPublisher;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetails;
import org.springframework.stereotype.Component;

@Component // Está siendo inyectada en el SpringSecurityConfig (vía constructor y usando la interfaz AuthenticationEventPublisher)
public class AuthenticationSuccessErrorHandler implements AuthenticationEventPublisher {

	private static final Logger LOG = LoggerFactory.getLogger(AuthenticationSuccessErrorHandler.class);

	@Override
	public void publishAuthenticationSuccess(Authentication authentication) {
		//Si la autentication es una instancia de WebAuthenticationDetails no hacemos nada ya que esta 
		//instancia contiene el username y password de la aplicación: frontEndApp y 12345
		if(authentication.getDetails() instanceof WebAuthenticationDetails) return;
		
		//Lo que queremos evaluar cuando sea una autenticación de éxito es cuando sea un usuario registrado en la BD
		UserDetails userDetails = (UserDetails) authentication.getPrincipal();
		System.out.printf("Success login: %s%n", userDetails.getUsername());
		LOG.info("Success login: {}", userDetails.getUsername());
	}

	@Override
	public void publishAuthenticationFailure(AuthenticationException exception, Authentication authentication) {
		System.out.printf("Error en el login: %s%n", exception.getMessage());
		LOG.error("Error en el login: {}", exception.getMessage());
	}

}
