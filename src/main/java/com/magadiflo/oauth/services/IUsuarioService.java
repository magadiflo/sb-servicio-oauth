package com.magadiflo.oauth.services;

import org.springframework.security.core.userdetails.UserDetailsService;

import com.magadiflo.commons.usuarios.models.entity.Usuario;

public interface IUsuarioService extends UserDetailsService {

	Usuario findByUsername(String username);
	
	Usuario update(Usuario usuario, Long id);

}
