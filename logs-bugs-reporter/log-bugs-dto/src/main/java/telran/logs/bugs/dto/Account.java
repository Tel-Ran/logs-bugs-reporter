package telran.logs.bugs.dto;

import java.util.Arrays;

public class Account {
		public String username;
		public String password;
		public String[] roles;
		public Account(String username, String password, String[] roles) {
			this.username = username;
			this.password = password;
			this.roles = roles;
		}
		public Account() {
		}
		@Override
		public String toString() {
			return "Account [username=" + username + ", roles=" + Arrays.toString(roles) + "]";
		}
	}