package pt.agroSmart.util;

public class RegisterData {

	public String username;
	public String password;
	public String confirmation;
	public String name;
	public String email;

	
	public RegisterData() {
		
	}
	
	public RegisterData(String username, String password, String confirmation, String name, String email) {
		this.username = username;
		this.password = password;
		this.confirmation = confirmation;
		this.name = name;
		this.email = email;
	}
	
	private boolean nonEmptyField(String field) {
		return field != null && !field.isEmpty();
	}
	public boolean validRegistration() {
		return nonEmptyField(username) && nonEmptyField(password) && nonEmptyField(confirmation) && 
			   nonEmptyField(email) && nonEmptyField(name) && email.contains("@") && password.equals(confirmation);
	}
}
