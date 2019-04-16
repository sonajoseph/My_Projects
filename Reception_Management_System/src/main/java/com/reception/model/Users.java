
	
	package com.reception.model;

	import java.util.Collection;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
	import javax.persistence.GenerationType;
	import javax.persistence.Id;
	
    import javax.persistence.OneToOne;
   
   import javax.persistence.Table;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonProperty.Access;

	@Entity

	@Table(name = "users")
//	public class Users implements UserDetails{
	public class Users{
		

		@Id
		
		@GeneratedValue(strategy=GenerationType.AUTO)
		@JsonProperty(access = Access.WRITE_ONLY) // hide username 
		private int userid;
		@JsonProperty(access = Access.WRITE_ONLY) 
		 @Column(unique = true)
		private String username;
	 
		@JsonProperty(access = Access.WRITE_ONLY) // hide password
		private String password;
		@JsonProperty(access=Access.WRITE_ONLY)
		private String roleid;
		
		
//		@OneToOne(fetch = FetchType.LAZY,
//	            cascade =  CascadeType.ALL,
//	            mappedBy = "user")
//	    private Receptionist receptionist;
//		
//		public Users(){
//			
//		}
//		
//		
//		 
//		 
//		
//		
//
//		public Users(String username, String password) {
//			super();
//			this.username = username;
//			this.password = password;
//			
//		}

		public String getRoleid() {
			return roleid;
		}

		public void setRoleid(String roleid) {
			this.roleid = roleid;
		}

		public int getUserid() {
			return userid;
		}

		public void setUserid(int userid) {
			this.userid = userid;
		}

		public String getUsername() {
			return username;
		}

		public void setUsername(String username) {
			this.username = username;
		}

		public String getPassword() {
			return password;
		}

		public void setPassword(String password) {
			this.password = password;
		}

//		@Override
//		public Collection<? extends GrantedAuthority> getAuthorities() {
//			// TODO Auto-generated method stub
//			return null;
//		}
//
//		@Override
//		public boolean isAccountNonExpired() {
//			// TODO Auto-generated method stub
//			return true;
//		}
//
//		@Override
//		public boolean isAccountNonLocked() {
//			// TODO Auto-generated method stub
//			return true;
//		}
//
//		@Override
//		public boolean isCredentialsNonExpired() {
//			// TODO Auto-generated method stub
//			return true;
//		}
//
//		@Override
//		public boolean isEnabled() {
//			// TODO Auto-generated method stub
//			return true;
//		}

		@Override
		public String toString() {
			return "Users [userid=" + userid + ", username=" + username + ", password=" + password + ", roleid="
					+ roleid + "]";
		}
		
		

		

		

		
		
		
		
	}
	
		