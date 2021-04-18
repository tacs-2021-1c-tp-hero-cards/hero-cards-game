package ar.edu.utn.frba.tacs.tp.api.herocardsgame.models.accounts;

public class User {

  private String username;
  private String fullName;
  private String password;
  private String token;

  public User(String username, String fullName, String password, String token) {
    this.username = username;
    this.fullName = fullName;
    this.password = password;
    this.token = token;
  }

  public String getUsername() {
    return username;
  }

  public String getPassword() {
    return password;
  }

  public String getToken() {
    return token;
  }

  public String getFullName() {
    return fullName;
  }
}
