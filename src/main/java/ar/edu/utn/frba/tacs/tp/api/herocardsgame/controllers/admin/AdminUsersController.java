package ar.edu.utn.frba.tacs.tp.api.herocardsgame.controllers.admin;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
@RequestMapping("/admin")
public class AdminUsersController {
  
  @GetMapping("/users")
  @RequestMapping(method = RequestMethod.GET, value = {"/users/{userId}"})
  public void getUsers(@PathVariable("userId") String userId) {
    System.out.println(userId);
  }

}
