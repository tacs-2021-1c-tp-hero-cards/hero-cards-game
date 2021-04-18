package ar.edu.utn.frba.tacs.tp.api.herocardsgame.controllers;

import static java.util.Collections.singletonList;

import ar.edu.utn.frba.tacs.tp.api.herocardsgame.models.accounts.Authentication;
import ar.edu.utn.frba.tacs.tp.api.herocardsgame.models.accounts.User;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
public class UsersController {

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
  @PostMapping("/signIn")
  public ResponseEntity<Authentication>  signIn(@RequestBody User user) {
    System.out.println("user: " + user.getUsername() );
    final String token = "eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCIsImtpZCI6IjEifQ";
    final Authentication auth = new Authentication(token);
    return ResponseEntity.ok().body(auth);
  }

  @GetMapping("/admin/users")
  @RequestMapping(method = RequestMethod.GET, value = {"/admin/users/{userId}"})
  public ResponseEntity<List<User>> getUsers(@PathVariable("userId") String userId) {
    System.out.println(userId);
    final User user = new User(userId, "fullname", "password", "token");
    final List<User> users = singletonList(user);
    return ResponseEntity.ok().body(users);
  }

  @PostMapping("/users/logout")
  public void logout() { }

}
