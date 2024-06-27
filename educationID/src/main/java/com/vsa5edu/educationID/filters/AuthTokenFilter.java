package com.vsa5edu.educationID.filters;

import com.vsa5edu.educationID.services.AuthenticationService;
import jakarta.servlet.*;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Controller;

import java.io.Console;
import java.io.IOException;
import java.util.Arrays;

//@Controller - auto bean init. If set url pattern - create registration bean
@Controller
@Order(1)
public class AuthTokenFilter implements Filter {
    @Autowired
    AuthenticationService authenticationService;
    @Override
    public void doFilter(ServletRequest servletRequest
            , ServletResponse servletResponse
            , FilterChain filterChain) throws IOException, ServletException {

        //add include and exclude path

        HttpServletRequest req = (HttpServletRequest) servletRequest;
        HttpServletResponse res = (HttpServletResponse) servletResponse;
        //start transaction
        //ignore /authentication path for tokens
        if (req.getRequestURL().toString().contains("/authentication")
                || req.getRequestURL().toString().contains("/oauth")){
            filterChain.doFilter(servletRequest, servletResponse);
            //end transaction
            return;
        }

        //check token
        Cookie[] cookies = req.getCookies();
        //Ищем куки
        if (cookies == null){
            res.setStatus(400);
            //?redirect login page?
            System.out.println("---Auth filter not find requred cookies!");
            return;
        }
        //Если куки есть - выцепляем токен
        String token = String.valueOf(Arrays.stream(req.getCookies())
                    .filter(e->e.getName().equals("access_token"))
                    .findFirst().map(e->e.getValue()));

        //check token
        try {
            authenticationService.checkJWT(token);
            //start controller
            filterChain.doFilter(req, res);
            return;
        }
        catch (Exception e){
            System.out.println("---Auth filter can't verify access token!");
            res.setStatus(444);
        }
        //end transaction
    }
}
