package account.configurations;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.AccessDeniedHandler;

@Configuration
@EnableWebSecurity
public class SecurityConfiguration {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .httpBasic()
                .authenticationEntryPoint(new RestAuthenticationEntryPoint()) // Handle auth error
                .and()
                .csrf().disable().headers().frameOptions().disable() // for Postman, the H2 console // disabling CSRF will allow sending POST request using Postman
                .and()
                .exceptionHandling().accessDeniedHandler(accessDeniedHandler())
                .and()
                .authorizeHttpRequests(authorize -> authorize
                        // to return 400 instead of 401 for open endpoints
                        // (endpoints redirecting to the /error/** in case of error and /error/ is secured by spring security)
                        .requestMatchers("/h2-console/**", "/error/**", "/actuator/shutdown").permitAll() // makes endpoint public
                        .requestMatchers("/api/auth/signup").permitAll()
                        .requestMatchers("/api/auth/changepass").hasAnyAuthority("ROLE_USER", "ROLE_ADMINISTRATOR","ROLE_ACCOUNTANT")
                        .requestMatchers("/api/empl/payment").hasAnyAuthority("ROLE_USER", "ROLE_ACCOUNTANT")
                        .requestMatchers("/api/acct/payments").hasAuthority("ROLE_ACCOUNTANT")
                        .requestMatchers("/api/admin/**").hasAuthority("ROLE_ADMINISTRATOR")
                        .requestMatchers("/api/security/**").hasAuthority("ROLE_AUDITOR")
                        .anyRequest().authenticated()
                )
                .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS); // no session
        return http.build();
    }

    @Bean
    public BCryptPasswordEncoder bCryptPasswordEncoder() {
        return new BCryptPasswordEncoder(13);
    }

    @Bean
    public AccessDeniedHandler accessDeniedHandler(){
        return new CustomAccessDeniedHandler();
    }
}
