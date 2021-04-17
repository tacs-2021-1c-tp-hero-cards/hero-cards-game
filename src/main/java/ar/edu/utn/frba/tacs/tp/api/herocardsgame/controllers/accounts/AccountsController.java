package ar.edu.utn.frba.tacs.tp.api.herocardsgame.controllers.accounts;

import ar.edu.utn.frba.tacs.tp.api.herocardsgame.models.accounts.Authentication;
import ar.edu.utn.frba.tacs.tp.api.herocardsgame.models.accounts.User;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@Controller
public class AccountsController {

  /**
   * TODO authentication with one of the following platforms
   * TODO We should validate username, password
   * (Google/FB/Github/LinkedIn)
   * @param user
   * @return
   */
  @PostMapping("/login")
  public ResponseEntity<Authentication> login(@RequestBody User user) {
    System.out.println("user: " + user.getUsername() );
    final String token = "eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCIsImtpZCI6IjEifQ";
    final Authentication auth = new Authentication(token);
    return ResponseEntity.ok().body(auth);
  }

  /**
   * TODO We should validate username, password and fullname
   * TODO We should validate if the username doesn't exists
   * TODO call the service to persist the new username
   * @param user
   * @return
   */
  @PostMapping("/sigIn")
  public ResponseEntity<Authentication>  sigIn(@RequestBody User user) {
    System.out.println("user: " + user.getUsername() );
    final String token = "eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCIsImtpZCI6IjEifQ";
    final Authentication auth = new Authentication(token);
    return ResponseEntity.ok().body(auth);
  }

}
