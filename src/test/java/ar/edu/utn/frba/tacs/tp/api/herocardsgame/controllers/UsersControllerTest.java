package ar.edu.utn.frba.tacs.tp.api.herocardsgame.controllers;

import static java.util.Collections.singletonList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.lenient;

import ar.edu.utn.frba.tacs.tp.api.herocardsgame.models.accounts.Authentication;
import ar.edu.utn.frba.tacs.tp.api.herocardsgame.models.accounts.User;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.platform.runner.JUnitPlatform;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@ExtendWith(MockitoExtension.class)
@RunWith(JUnitPlatform.class)
class UsersControllerTest {

  @InjectMocks
  UsersController usersController;

  @Test
  void login() {
    final Authentication auth = new Authentication("token");

    MockHttpServletRequest request = new MockHttpServletRequest();
    RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));

    UsersController mock = org.mockito.Mockito.mock(UsersController.class);

    final User user = new User("user1", "", "password", "");

    lenient().when(mock.login(user)).thenReturn(ResponseEntity.ok().body(auth));

    ResponseEntity<Authentication> responseEntity = usersController.login(user);

    assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
    Authentication body = responseEntity.getBody();
    assertThat(body.getToken()).isEqualTo("eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCIsImtpZCI6IjEifQ");
  }

  @Test
  void sigIn() {
    final Authentication auth = new Authentication("token");

    MockHttpServletRequest request = new MockHttpServletRequest();
    RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));

    UsersController mock = org.mockito.Mockito.mock(UsersController.class);

    final User user = new User("user1", "fullname", "password", "token");

    lenient().when(mock.signIn(user)).thenReturn(ResponseEntity.ok().body(auth));

    ResponseEntity<Authentication> responseEntity = usersController.signIn(user);

    assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
    Authentication body = responseEntity.getBody();
    assertThat(body.getToken()).isEqualTo("eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCIsImtpZCI6IjEifQ");
  }

  @Test
  void getUsers() {
    MockHttpServletRequest request = new MockHttpServletRequest();
    RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));

    UsersController mock = org.mockito.Mockito.mock(UsersController.class);

    final User user = new User("user1", "fullname", "password", "token");
    final List<User> users = singletonList(user);

    lenient().when(mock.getUsers("user1")).thenReturn(ResponseEntity.ok().body(users));

    ResponseEntity<List<User>> responseEntity = usersController.getUsers("user1");

    assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
    List<User> body = responseEntity.getBody();
    assertThat(body).hasSize(1);
    assertThat(body.get(0).getFullName()).isEqualTo("fullname");
    assertThat(body.get(0).getToken()).isEqualTo("token");
    assertThat(body.get(0).getUsername()).isEqualTo("user1");
  }

  @Test
  void logout() {
  }
}