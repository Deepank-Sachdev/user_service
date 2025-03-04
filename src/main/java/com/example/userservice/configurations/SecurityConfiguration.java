//package com.example.userservice.configurations;
//
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.security.config.annotation.web.builders.HttpSecurity;
//import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
//import org.springframework.security.web.SecurityFilterChain;
//
//@Configuration
//public class SecurityConfiguration {
//    @Bean
//    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
//        http
//                .authorizeHttpRequests((requests) -> requests.anyRequest()
//                        .permitAll())
//                        .cors(AbstractHttpConfigurer::disable)
//                        .csrf(AbstractHttpConfigurer::disable);
//
//        return http.build();
//    }
//}
