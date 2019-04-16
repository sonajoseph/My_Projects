


	package com.reception.serviceimpl;

	import java.util.Arrays;
	import java.util.List;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
	import org.springframework.security.core.authority.SimpleGrantedAuthority;
	import org.springframework.security.core.userdetails.UserDetails;
	import org.springframework.security.core.userdetails.UserDetailsService;
	import org.springframework.security.core.userdetails.UsernameNotFoundException;
	import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
	import org.springframework.stereotype.Service;


import com.reception.model.Receptionist;
import com.reception.model.Role;
//	import com.reception.enums.Role;
	import com.reception.model.Users;
import com.reception.repository.RoleRepository;
import com.reception.repository.UserRepository;

import com.reception.service.UserService;


	@Service(value = "userService")

	public class UserServiceImpl implements UserDetailsService, UserService {
		@Autowired
		private UserRepository userrepo;
		@Autowired
		private BCryptPasswordEncoder bcryptEncoder;
		@Autowired
		private RoleRepository roleRepo;

		public UserDetails loadUserByUsername(String userid) throws UsernameNotFoundException {
			System.out.println("I am HERE---ttttttttttttttttttt" + userid);
			
			Users user = userrepo.findByUsername(userid);
			System.err.println("---------" + user);
			if (user == null) {
				throw new UsernameNotFoundException("Invalid username or password.");
			}
			return new org.springframework.security.core.userdetails.User(user.getUsername(), user.getPassword(),
					getAuthority(user.getRoleid()));
		
			
		}
		

		

		private List<SimpleGrantedAuthority> getAuthority(String role) {
			
			
			Role roles = roleRepo.findOne(Integer.parseInt(role));
			System.out.println("role---------------------------------------------------->"+roles.getRoleName());
			
			return Arrays.asList(new SimpleGrantedAuthority("ROLE_" + roles.getRoleName()));
		}

		public Users save(Users user) {
			user.setRoleid("1");
			user.setPassword(bcryptEncoder.encode(user.getPassword()));
			userrepo.save(user);
			 return null;
			}
		}

			
			



	
			 
			 
		


