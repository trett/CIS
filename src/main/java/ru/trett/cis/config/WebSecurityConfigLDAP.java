package ru.trett.cis.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.authority.mapping.GrantedAuthoritiesMapper;
import org.springframework.security.core.authority.mapping.SimpleAuthorityMapper;
import org.springframework.security.web.csrf.CsrfFilter;
import org.springframework.web.filter.CharacterEncodingFilter;

@Configuration
@Profile("ldap")
@PropertySource("/WEB-INF/resources/ldap.properties")
@EnableWebSecurity
public class WebSecurityConfigLDAP extends WebSecurityConfigurerAdapter {


    @Autowired
    Environment env;

    public void configure(WebSecurity web) throws Exception {
        web
                .ignoring()
                .antMatchers("/webjars/**")
                .antMatchers("/css/**")
                .antMatchers("/images/**");
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        CharacterEncodingFilter filter = new CharacterEncodingFilter();
        filter.setEncoding("UTF-8");
        filter.setForceEncoding(true);
        http.addFilterBefore(filter, CsrfFilter.class);

        http
                .authorizeRequests()
                .antMatchers("/**").hasRole(env.getProperty("role"))
                .anyRequest().authenticated()
                .and()
                .formLogin()
                .loginPage("/login")
                .permitAll()
                .defaultSuccessUrl("/home")
                .failureForwardUrl("/login-error")
                .and()
                .exceptionHandling()
                .accessDeniedPage("/error")
                .and()
                .rememberMe()
                .key("asdfqwezxc2wsx")
                .rememberMeCookieName("remember-me")
                .tokenValiditySeconds(3600)
                .and()
                .csrf().disable();
    }

    @Autowired
    public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
        auth
                .ldapAuthentication()
                .groupSearchBase(env.getProperty("group.search.base"))
                .groupSearchFilter(env.getProperty("group.search.filter"))
                .groupRoleAttribute("cn")
                .userSearchBase(env.getProperty("user.search.base"))
                .userSearchFilter(env.getProperty("user.search.filter"))
                .authoritiesMapper(authoritiesMapper())
                .contextSource()
                .url(env.getProperty("url"))
                .managerDn(env.getProperty("manager.dn"))
                .managerPassword(env.getProperty("manager.password"));
    }

    @Bean
    public GrantedAuthoritiesMapper authoritiesMapper() {
        SimpleAuthorityMapper authoritiesMapper = new SimpleAuthorityMapper();
        authoritiesMapper.setConvertToUpperCase(true);
        return authoritiesMapper;
    }

}
