package ar.edu.utn.frba.tacs.tp.api.herocardsgame.models.accounts;

public class Authentication {

  private String token;

  public Authentication(String token) {
    this.token = token;
  }

  public String getToken() {
    return token;
  }
}
