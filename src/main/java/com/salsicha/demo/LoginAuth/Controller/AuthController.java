package com.salsicha.demo.LoginAuth.Controller;

import com.salsicha.demo.LoginAuth.Model.DTO.CredDto;
import com.salsicha.demo.LoginAuth.Model.User;
import com.salsicha.demo.LoginAuth.Security.JWTUtil;
import com.salsicha.demo.LoginAuth.Service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.IOException;
import java.net.URI;
import java.util.Date;
import java.util.concurrent.ExecutionException;

@RestController
@RequestMapping(value = "/auth")
public class AuthController {
    @Autowired
    private UserService userService;
    @Autowired
    private JWTUtil jwtUtil;
    @Autowired
    private AuthenticationManager authenticationManager;



    @PostMapping
    public ResponseEntity<Void> addUser(@RequestBody @Valid User user){
        User u =userService.insert(user);
        URI uri = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}").buildAndExpand(u.getId()).toUri();
        return ResponseEntity.created(uri).build();

    }
    @GetMapping("/{id}")
    public ResponseEntity<?> findById(@PathVariable Long id){
        User user = userService.findById(id);
        return ResponseEntity.ok().body(user);
    }
    @GetMapping(value = "/email={email}")
    public ResponseEntity<User> findEmail(@PathVariable String email) {
        User obj = userService.findByEmail(email);
        return ResponseEntity.ok().body(obj);
    }











    @PostMapping("/authenticated")
    public String LoginToken(@RequestBody CredDto cred){
        Object s = userService.login(cred);
        System.out.println(s);
        if(s == null){
            return "401";
        }else{
            try {
                authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(cred.getEmail(), cred.getPassword()));
            }catch (Exception e){
                new JWTAuthenticationFailureHandler();
            }
            return "Bearer "+jwtUtil.generateToken(cred.getEmail()) ;
        }
    }
    @PostMapping("/refresh_token")
    public ResponseEntity<Void> refreshToken(HttpServletResponse response, @RequestBody String email) throws ExecutionException, InterruptedException {
        User user = userService.findByEmail(email);
        String token = jwtUtil.generateToken(user.getEmail());
        response.addHeader("Authorization", "Bearer " + token);
        response.addHeader("access-control-expose-headers", "Authorization");
        return ResponseEntity.noContent().build();
    }

    private class JWTAuthenticationFailureHandler implements AuthenticationFailureHandler {
        @Override
        public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception)
                throws IOException, ServletException {
            response.setStatus(401);
            response.setContentType("application/json");
            response.getWriter().append(json());
        }

        private String json() {
            long date = new Date().getTime();
            return "{\"timestamp\": " + date + ", "
                    + "\"status\": 401, "
                    + "\"error\": \"Não autorizado\", "
                    + "\"message\": \"Email ou senha inválidos\", "
                    + "\"path\": \"/login\"}";
        }

    }
}
