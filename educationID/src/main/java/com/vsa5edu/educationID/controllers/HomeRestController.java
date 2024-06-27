package com.vsa5edu.educationID.controllers;

import com.vsa5edu.educationID.database.DTO.TrustedService;
import com.vsa5edu.educationID.database.DTO.User;
import com.vsa5edu.educationID.database.DatabaseContext;
import com.vsa5edu.educationID.services.AuthenticationService;
import jakarta.persistence.EntityManager;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ResourceLoader;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

@RestController
@CrossOrigin(originPatterns = "*", allowCredentials = "true")
public class HomeRestController {
    @GetMapping("/home")
    String home(){
        return "Hello World!";
    }
    ////test
    @Autowired(required = true)
    DatabaseContext context;

    @Autowired
    AuthenticationService authenticationService;
    @GetMapping("/test")
    String test(HttpServletRequest request){
        Cookie c;
        if ((c = Arrays.stream(request.getCookies()).findFirst().get())!=null){
            return c.getValue();
        }
        return "PONG!";
    }

    @PostMapping("/gettoken")
    void getToken(HttpServletResponse response){
        //Cookie tokenCookie = new Cookie("token","1");
        //tokenCookie.setHttpOnly(true);
        //tokenCookie.setSecure(true);
        // tokenCookie.setMaxAge();

        ResponseCookie rc = ResponseCookie.from("token", "1")
                .httpOnly(true)
                        .secure(false)
                                .sameSite("none")
                                        .build();


        response.setHeader(HttpHeaders.SET_COOKIE, rc.toString());
    }

    @PostMapping("/update-test")
    @ResponseStatus(HttpStatus.CREATED)
    @Transactional
    void updateTest() throws NoSuchAlgorithmException {
        User u = context.streamOf(User.class)
                .filter(e -> e.login.equals("test"))
                .findFirst().get();
        u.passwordSalt = authenticationService.generateSalt();
        u.passwordHash = authenticationService.getHash256WithSalt("test", u.passwordSalt);
        EntityManager em = context.getManager();
        em.persist(u);
        em.close();
    }
}
