package com.batch.demo.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)
public class WebSecurityConfig {

    private final String[] IGONRE_PATH = {
        "/",
        "/login.html",
        "/js/**",
        "/launch",
        "/csrfToken",
        "/graphiql",
        "/graphql-ws",
    }; 

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity){
         try {
            return httpSecurity
                    .authorizeHttpRequests()
                    .requestMatchers(IGONRE_PATH).permitAll()
                    .requestMatchers("/user/**").hasRole("USER")
                    .requestMatchers("/vip/**").hasRole("VIP")
                    .requestMatchers("/admin/**").hasRole("ADMIN")
                    .anyRequest().authenticated()
                    .and()
                        .headers().frameOptions().sameOrigin()
                    .and()
                    .formLogin()
                        .loginProcessingUrl("/login")
                        .loginPage("/login").permitAll()                    
                        .defaultSuccessUrl("/", true)
                    .and()
                        .logout()
                        .logoutUrl("/logout")
                        .logoutSuccessUrl("/")
                    .and()
                    .build();
        } catch (Exception e) {
            e.printStackTrace();
        }            
        return null;
    }    

    @Bean 
    public PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }
}