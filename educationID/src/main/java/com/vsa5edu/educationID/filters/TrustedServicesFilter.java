package com.vsa5edu.educationID.filters;

import com.vsa5edu.educationID.services.AuthenticationService;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Controller;

import java.io.IOException;
import java.net.URL;

//@Controller
//@Order(2)
public class TrustedServicesFilter implements Filter {

    @Autowired
    AuthenticationService authenticationService;
    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest) servletRequest;
        HttpServletResponse res = (HttpServletResponse) servletResponse;

        //check refer and location domains
        //get host request
        String auth_domain = req.getRequestURL().toString();// .getHeader("host");
        //for trusted services
        String redirect_service_domain = req.getHeader("Refer");

        //check refer domain
        if (redirect_service_domain!=null){
            URL url_redirect = new URL(redirect_service_domain);
            redirect_service_domain = url_redirect.getHost();
            try {
                authenticationService.getTrustedService(redirect_service_domain);
            }
            catch (Exception e){
                //redirect service is not valid!
                res.setStatus(400);
                System.out.println("!!! Редирект-Запрос с недовереннного сервиса. Это фильтр 1.");
                return;
            }
        }
        //check auth domain
        try {
            authenticationService.getTrustedService(auth_domain);
        }
        catch (Exception e){
            //redirect service is not valid!
            res.setStatus(400);
            System.out.println("!!! Запрос с недовереннного сервиса. Это фильтр 1.");
            return;
        }

        filterChain.doFilter(req, res);
    }
}
