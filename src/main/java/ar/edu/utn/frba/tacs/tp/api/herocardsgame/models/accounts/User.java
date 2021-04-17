package ar.edu.utn.frba.tacs.tp.api.herocardsgame.models.accounts;

public class User {

  private String username;
  private String fullname;
  private String password;
  private String token;

  public String getUsername() {
    return username;
  }

  public String getPassword() {
    return password;
  }

  public String getToken() {
    return token;
  }

  public String getFullname() {
    return fullname;
  }
}
