package com.magadiflo.oauth.services;

import com.magadiflo.commons.usuarios.models.entity.Usuario;

public interface IUsuarioService {
	
	Usuario findByUsername(String username);

}
