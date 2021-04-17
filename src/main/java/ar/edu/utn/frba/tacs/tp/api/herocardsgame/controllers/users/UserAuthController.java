package ar.edu.utn.frba.tacs.tp.api.herocardsgame.controllers.users;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/users")
public class UserAuthController {


  @PostMapping("/logout")
  public void logout() { }

}
