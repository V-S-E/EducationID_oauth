package com.vsa5edu.educationID.filters;

import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

//Не обязательно. Если у класса фильтра есть аннотация Filter, то его бин конфигурируется по-умолчанию.
@Component
public class FilterRegistrationConfiguration {

/*    @Bean
    public FilterRegistrationBean<AuthTokenFilter> authTokenFilterRegistrationBean(){
        FilterRegistrationBean<AuthTokenFilter> registrationBean = new FilterRegistrationBean<>();
        registrationBean.setFilter(new AuthTokenFilter());
        registrationBean.addUrlPatterns("*");
        registrationBean.setOrder(2);
        return registrationBean;
    }*/

/*    @Bean
    public FilterRegistrationBean<TrustedServicesFilter> trustedServiceFilterRegistrationBean(){
        FilterRegistrationBean<TrustedServicesFilter> registrationBean = new FilterRegistrationBean<>();
        registrationBean.setFilter(new TrustedServicesFilter());
        registrationBean.addUrlPatterns("*");
        registrationBean.setOrder(1);
        return registrationBean;
    }*/
}
