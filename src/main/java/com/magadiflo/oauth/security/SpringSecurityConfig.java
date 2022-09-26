package com.magadiflo.oauth.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationEventPublisher;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class SpringSecurityConfig extends WebSecurityConfigurerAdapter {

	private final UserDetailsService usuarioService;
	
	private final AuthenticationEventPublisher authenticationEventPublisher;

	public SpringSecurityConfig(UserDetailsService usuarioService, AuthenticationEventPublisher authenticationEventPublisher) {
		this.usuarioService = usuarioService;
		this.authenticationEventPublisher = authenticationEventPublisher;
	}

	@Override
	protected void configure(AuthenticationManagerBuilder auth) throws Exception {
		auth.userDetailsService(this.usuarioService).passwordEncoder(passwordEncoder())
			.and()
			.authenticationEventPublisher(this.authenticationEventPublisher);
	}

	//Necesitamos registrarlo en el contenedor de Spring porque más adelante se usará en la configuración del servidor de autorización
	@Bean
	@Override
	protected AuthenticationManager authenticationManager() throws Exception {
		return super.authenticationManager();
	}

	//Necesitamos registrarlo en el contenedor de Spring porque más adelante se usará en la configuración del servidor de autorización
	@Bean
	public static PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

}
/**
 * NOTA: 
 * Para evitar un posible error en las ultimas versiones de spring boot 2.6.0 en adelante:
 * 		"BeanCurrentlyInCreationException: Error creating bean with name 'springSecurityConfig': 
 * 		Requested bean is currently in creation: Is there an unresolvable circular reference"
 * Es que definimos el método passwordEncoder() como un método estático
 */