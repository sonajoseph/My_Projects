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
import org.springframework.transaction.annotation.Transactional;

import com.reception.dto.ReceptionistDTO;
import com.reception.model.Receptionist;
import com.reception.model.Users;
import com.reception.repository.ReceptionistRepository;
import com.reception.repository.UserRepository;
import com.reception.service.ReceptionistService;
@Service
public class ReceptionistServiceImpl implements  UserDetailsService,ReceptionistService{
	@Autowired
	private UserRepository userRepo;
	 @Autowired
	  private BCryptPasswordEncoder encoder;
	 @Autowired 
	private  ReceptionistRepository receptionistRepo;
	 @Override
		public UserDetails loadUserByUsername(String userid) throws UsernameNotFoundException {
			
	        System.out.println("I am HERE----");
			
			Users user = userRepo.findByUsername(userid);
			System.err.println("---------" + user);
			if (user == null) {
				throw new UsernameNotFoundException("Invalid username or password.");
			}
//			return user;
			return new org.springframework.security.core.userdetails.User(user.getUsername(), user.getPassword(),
					getAuthority(user.getRoleid()));
			
			
		}
			
	 			

		private List<SimpleGrantedAuthority> getAuthority(String role) {
			
			System.out.println("role---------------------------------------------------->"+role);
			
			return Arrays.asList(new SimpleGrantedAuthority(role));
		}
	 
	 
	 
	 
	 
	@Transactional 
	@Override
	public void saveReceptionist(ReceptionistDTO receptionist) {
		Users user = new Users();
		Receptionist receptionists =new Receptionist();
		BeanUtils.copyProperties(receptionist, user);
//		user.setRole( Role.ROLE_RECEPTIONIST.toString());
		user.setRoleid("2");
		user.setPassword(encoder.encode(user.getPassword()));
//		userRepo.save(user);
		BeanUtils.copyProperties(receptionist, receptionists);
		receptionists.setUser(user);
		receptionistRepo.save(receptionists);
	}

	@Override
	public Receptionist save(Receptionist receptionist) {
		
		return receptionistRepo.save(receptionist);
	}

	
	}

	



	
	

