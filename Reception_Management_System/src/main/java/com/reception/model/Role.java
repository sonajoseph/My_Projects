package com.reception.model;





	import java.util.List;

	import javax.persistence.Entity;
	import javax.persistence.GeneratedValue;
	import javax.persistence.GenerationType;
	import javax.persistence.Id;
	import javax.persistence.OneToMany;
	import javax.persistence.Table;

	@Entity
	@Table(name = "roles")

	public class Role {
		
	    @Id
		
		@GeneratedValue(strategy=GenerationType.AUTO)
		private int roleId;
		private String roleName;
		
		public int getRoleId() {
			return roleId;
		}
		public void setRoleId(int roleId) {
			this.roleId = roleId;
		}
		public String getRoleName() {
			return roleName;
		}
		public void setRoleName(String roleName) {
			this.roleName = roleName;
		}
		public Role(int roleId, String roleName) {
			super();
			this.roleId = roleId;
			this.roleName = roleName;
		}
		public Role() {
			super();
		}
		
		
		
		
		
	}





