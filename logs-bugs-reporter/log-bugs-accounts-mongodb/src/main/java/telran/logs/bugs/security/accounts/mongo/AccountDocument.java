package telran.logs.bugs.security.accounts.mongo;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection="accounts")
public class AccountDocument {

@Id
String username;
long activationTimestamp;
String password;
String [] roles;
long expirationTimestamp;
public AccountDocument() {
}
public AccountDocument(String username, long activationTimestamp, String password, String[] roles,
		long expirationTimestamp) {
	super();
	this.username = username;
	this.activationTimestamp = activationTimestamp;
	this.password = password;
	this.roles = roles;
	this.expirationTimestamp = expirationTimestamp;
}
public long getActivationTimestamp() {
	return activationTimestamp;
}
public void setActivationTimestamp(long activationTimestamp) {
	this.activationTimestamp = activationTimestamp;
}
public String getPassword() {
	return password;
}
public void setPassword(String password) {
	this.password = password;
}
public String[] getRoles() {
	return roles;
}
public void setRoles(String[] roles) {
	this.roles = roles;
}
public long getExpirationTimestamp() {
	return expirationTimestamp;
}
public void setExpirationTimestamp(long expirationTimestamp) {
	this.expirationTimestamp = expirationTimestamp;
}
public String getUsername() {
	return username;
}


}
