package com.vsa5edu.educationID.controllers;

import com.auth0.jwt.interfaces.DecodedJWT;
import com.vsa5edu.educationID.database.DTO.TrustedService;
import com.vsa5edu.educationID.database.DTO.User;
import com.vsa5edu.educationID.services.AuthenticationService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.Arrays;
import java.util.Optional;

//only for account page
@RestController
@RequestMapping("/authentication")
//only for account page and local postman
@CrossOrigin(origins = {"http://localhost:3000", "http://localhost:8089"}, allowCredentials = "true")
public class AuthenticationRestController {

    @Autowired
    AuthenticationService authenticationService;

    final String jwt_cookie_name = "access_token";
    final String refresh_cookie_name = "refresh_token";

    //get password salt (alg - sha256)
    @GetMapping("/get-salt")
    @ResponseBody
    public String getPasswordHash(@RequestParam(required = false) String login
            , @RequestParam(required = false) String phone) throws Exception {
        String salt = authenticationService.getUserPasswordSalt(login, phone);
        return salt;
    }

    //authentication - redirect to service with authorization token
    @PostMapping("/login-token")
    public String login(@RequestBody AuthenticationBodyRequest bodyRequest
            , HttpServletRequest request, HttpServletResponse response) throws IOException {
        //auth domain - id 5
        String auth_domain = request.getHeader("host");
        //user agent for session
        String userAgent = request.getHeader("User-Agent");
        //for trusted services
        String fromDomain = request.getHeader("Refer");
        User user = null;

        //check account cookie
        if (null!=request.getCookies()) {
            Optional<Cookie> find_jwt_cookie = Arrays.stream(request.getCookies())
                    .filter(c -> c.getName().equals(jwt_cookie_name))
                    .findFirst();
            if (find_jwt_cookie.isPresent()){
                //check jwt refresh
                DecodedJWT jwt;
                try {
                    jwt = authenticationService.checkJWT(find_jwt_cookie.get().getValue());
                }
                catch (Exception e){
                    response.setStatus(444);
                    return "Error: "+e.getMessage();
                }
                //get user from jwt
                try {
                    user = authenticationService.getUserById(Long.parseLong(jwt.getSubject()));
                }
                catch (Exception e){
                    response.setStatus(400);
                    return "Error: "+e.getMessage();
                }

            }
        }
        else {
            try {
                user = authenticationService.checkAuthentication(bodyRequest.getPasswordHash(), bodyRequest.getLogin(), bodyRequest.getPhone());
            }
            catch (Exception e){
                response.setStatus(400);
                return "Error: "+e.getMessage();
            }
        }

        TrustedService service = null;
        String authToken;
        try {
            service = authenticationService.getTrustedService(fromDomain);
            authToken = authenticationService.getSignAuthorizationToken(user, service, userAgent);
        }
        catch (Exception e){
            response.setStatus(400);
            return "Error: "+e.getMessage();
        }

        //check trust user for redirect service

        response.setStatus(301);
        response.sendRedirect(service.domainName+service.redirectUrl+"?token="+authToken);
        return "OK!";
    }

    //registration account
    public void registration(){
        //get login
        //get password
        //get phone
        //get mail
        //get firstname
        //get secname

        //check regex fields

        //generate salt
        //create new user - build
        //insert in database
        //redirect /login
    }

    public static class AuthenticationBodyRequest{
        private String login;
        private String phone;
        private String passwordHash;


        public void setLogin(String login) {
            this.login = login;
        }

        public String getLogin(){
            return login;
        }

        public void setPhone(String phone) {
            this.phone = phone;
        }
        public String getPhone(){
            return phone;
        }

        public void setPasswordHash(String passwordHash) {
            this.passwordHash = passwordHash;
        }
        public String getPasswordHash(){
            return passwordHash;
        }
    }
}
