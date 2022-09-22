package com.magadiflo.oauth.services;

import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.magadiflo.commons.usuarios.models.entity.Usuario;
import com.magadiflo.oauth.clients.UsuarioFeignClient;

@Service
public class UsuarioService implements IUsuarioService {

	private static final Logger LOG = LoggerFactory.getLogger(UsuarioService.class);
	private final UsuarioFeignClient client;

	public UsuarioService(UsuarioFeignClient client) {
		this.client = client;
	}

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		Usuario usuario = this.findByUsername(username);
		if (usuario == null) {
			LOG.error("Error en el login, no existe el usuario {} en el sistema", username);
			throw new UsernameNotFoundException(
					String.format("Error en el login, no existe el usuario %s en el sistema", username));
		}
		List<GrantedAuthority> authorities = usuario.getRoles().stream()
				.map(role -> new SimpleGrantedAuthority(role.getNombre()))
				.peek(authority -> LOG.info("Role: {}", authority.getAuthority())).collect(Collectors.toList());
		LOG.info("Usuario autenticado: {}", username);
		return new User(usuario.getUsername(), usuario.getPassword(), usuario.getEnabled(), true, true, true,
				authorities);
	}

	@Override
	public Usuario findByUsername(String username) {
		return this.client.findByUsername(username);
	}

}
