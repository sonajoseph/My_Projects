package com.uvt.faceRecognition.model;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.uvt.faceRecognition.service.utils.Role;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * 
 * @author Arun Johnson
 *
 */
@ToString
@Setter
@Getter
@Document
public class User implements UserDetails {

	private static final long serialVersionUID = 1L;

	@Id
	private String id;

	private String username;

	private String name;
	
	private String email;
	private String employeeId;

	private Role role;

	private String password;
	private boolean enabled;
	private String fileName;
	

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		if (role == null)
			return Collections.emptyList();
		return Arrays.asList(new SimpleGrantedAuthority(role.name()));
	}

	@Override
	public boolean isAccountNonExpired() {
		return true;
	}

	@Override
	public boolean isAccountNonLocked() {
		return true;
	}

	@Override
	public boolean isCredentialsNonExpired() {
		return true;
	}

	public boolean isEnabled() {
		return enabled;
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
		System.out.println("enabled : "+enabled); 
	}

	 

}
