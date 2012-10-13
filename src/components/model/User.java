/** @Pulkit
 * Use as a bean to store all user registration details.
 * Usage:
 * 	1. For login details, use the constructor User(username, password).
 *  2. For registration details, use the constructor User(all details...).
 *  3. To test whether this bean is about registration or login, use:
 *  	isFullRegistration() - returns true for registration, false for login
 * 
 */
package components.model;


public class User{
	private String firstName = null;
	private String surname = null;
	private String address = null;
	private String phone = null;
	private String username = null;
	private String password = null;

	public User(String username, String password){
		super();
		this.username = username;
		this.password = password;
	}

	public User(String firstName, String surname, String address, String phone, String username, String password){
		super();
		this.firstName = firstName;
		this.surname = surname;
		this.address = address;
		this.phone = phone;
		this.username = username;
		this.password = password;
	}

	public String getFirstName(){
		return firstName;
	}

	public String getSurname(){
		return surname;
	}

	public String getAddress(){
		return address;
	}

	public String getPhone(){
		return phone;
	}

	public String getUsername(){
		return username;
	}

	public String getPassword(){
		return password;
	}

	public void setFirstName(String firstName){
		this.firstName = firstName;
	}

	public void setSurname(String surname){
		this.surname = surname;
	}

	public void setAddress(String address){
		this.address = address;
	}

	public void setPhone(String phone){
		this.phone = phone;
	}

	public void setUsername(String username){
		this.username = username;
	}

	public void setPassword(String password){
		this.password = password;
	}

	public boolean isFullRegistration(){
		return firstName != null && !firstName.equals("") && !firstName.equals("null");
	}
}
