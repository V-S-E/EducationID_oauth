package com.vsa5edu.educationID.controllers;

import com.vsa5edu.educationID.services.AuthenticationService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.util.Pair;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.Optional;

@RestController
@RequestMapping("/oauth")
public class OAuthRestController {

    @Autowired
    AuthenticationService authenticationService;
    final String jwt_cookie_name = "access_token";
    final String refresh_cookie_name = "refresh_token";

    @GetMapping("/login")
    public String exchangeToken(@RequestParam String token, HttpServletRequest request
            , HttpServletResponse response){
        Pair<String,String> jwt_refresh;
        try {
            jwt_refresh = authenticationService.exchangeTokenOnAccessRefresh(token);
        }
        catch (Exception e){
            response.setStatus(400);
            return "Error: "+e.getMessage();
        }
        Cookie jwt_cookie = new Cookie(jwt_cookie_name, jwt_refresh.getFirst());
        jwt_cookie.setHttpOnly(true);
        jwt_cookie.setSecure(false);
        jwt_cookie.setPath("/oauth");
        jwt_cookie.setAttribute("same-site","none");

        Cookie refresh_cookie = new Cookie(refresh_cookie_name, jwt_refresh.getSecond());
        refresh_cookie.setHttpOnly(true);
        refresh_cookie.setSecure(false);
        refresh_cookie.setPath("/oauth");
        refresh_cookie.setAttribute("same-site","none");

        response.addCookie(jwt_cookie);
        response.addCookie(refresh_cookie);
        return "OK!";
    }

    @GetMapping("/logout")
    public String logout(HttpServletRequest request, HttpServletResponse response) {
        Cookie cookie = Arrays.stream(request.getCookies())
                .filter(c -> c.getName().equals(jwt_cookie_name))
                .findFirst().get();
        try {
            authenticationService.deleteSession(cookie.getValue());
        }
        catch (Exception e){
            response.setStatus(444);
            return "Error: "+e.getMessage();
        }
        Cookie jwt_cookie = new Cookie(jwt_cookie_name, "");
        jwt_cookie.setMaxAge(1);
        jwt_cookie.setSecure(false);
        jwt_cookie.setAttribute("same-site","none");
        jwt_cookie.setHttpOnly(true);
        jwt_cookie.setPath("/oauth");

        Cookie refresh_cookie = new Cookie(refresh_cookie_name, "");
        refresh_cookie.setAttribute("same-site","none");
        refresh_cookie.setHttpOnly(true);
        refresh_cookie.setSecure(false);
        refresh_cookie.setMaxAge(1);
        refresh_cookie.setPath("/oauth");

        response.addCookie(jwt_cookie);
        response.addCookie(refresh_cookie);
        //redirect?
        return "OK!";
    }

    //refresh tokens
    @GetMapping("/refresh")
    public String refreshToken(HttpServletRequest request, HttpServletResponse response){
        Optional<Cookie> refresh_cookie = Arrays.stream(request.getCookies())
                .filter(c -> c.getName().equals(refresh_cookie_name))
                .findFirst();

        if (refresh_cookie.isEmpty()){
            response.setStatus(400);
            return "Error: not find refresh token in cookies";
        }

        Pair<String,String> jwt_refresh;
        try{
            String refresh_token = refresh_cookie.get().getValue();
            jwt_refresh = authenticationService.refreshJWT(refresh_token);
        }
        catch (Exception e){
            response.setStatus(400);
            return "Error: "+e.getMessage();
        }

        Cookie new_jwt_cookie = new Cookie(jwt_cookie_name, jwt_refresh.getFirst());
        new_jwt_cookie.setSecure(false);
        new_jwt_cookie.setAttribute("same-site","none");
        new_jwt_cookie.setHttpOnly(true);
        new_jwt_cookie.setPath("/oauth");

        Cookie new_refresh_cookie = new Cookie(refresh_cookie_name, jwt_refresh.getSecond());
        new_refresh_cookie.setAttribute("same-site","none");
        new_refresh_cookie.setHttpOnly(true);
        new_refresh_cookie.setSecure(false);
        new_refresh_cookie.setPath("/oauth");

        response.addCookie(new_jwt_cookie);
        response.addCookie(new_refresh_cookie);

        return "OK!";
    }

    //get user data
}
