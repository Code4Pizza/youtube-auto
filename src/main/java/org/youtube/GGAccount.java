package org.youtube;

public class GGAccount {
	private String email;
	private String password;
	private String backup;

	public GGAccount() {
		super();
	}
	
	public GGAccount(String email, String password, String backup) {
		super();
		this.email = email;
		this.password = password;
		this.backup = backup;
	}
	
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public String getBackup() {
		return backup;
	}
	public void setBackup(String backup) {
		this.backup = backup;
	}
	
	
}
