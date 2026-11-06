package sw2.lab5.Config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.JdbcUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

import javax.sql.DataSource;

@Configuration
@EnableWebSecurity
public class WebSecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        // Los formularios de esta app no incluyen token CSRF (no se agrego el dialecto
        // thymeleaf-extras-springsecurity5), por eso se desactiva para que POST funcione.
        http.csrf().disable();

        http.formLogin()
                .loginPage("/loginForm")
                .loginProcessingUrl("/processlogin")
                .defaultSuccessUrl("/redirectByRol", true);

        http.logout()
                .logoutRequestMatcher(new org.springframework.security.web.util.matcher.AntPathRequestMatcher("/logout", "GET"))
                .logoutSuccessUrl("/")
                .deleteCookies("JSESSIONID")
                .invalidateHttpSession(true);

        http.authorizeRequests()
                // Administracion de usuarios: solo admin.
                .antMatchers("/user/list", "/user/borrar").hasAuthority("admin")
                // Perfil propio, edicion y amistades: cualquier usuario autenticado
                // (la logica de "solo puedo editar lo mio" se valida en el controller).
                .antMatchers("/user", "/user/editar", "/user/guardar",
                        "/user/amigos", "/user/amistad/**").hasAnyAuthority("user", "admin")
                // Posts: crear/editar/borrar requieren estar autenticado; la propiedad
                // del post (dueno vs admin) tambien se valida en PostController.
                .antMatchers("/post/create", "/post/edit", "/post/guardar", "/post/borrar")
                    .hasAnyAuthority("user", "admin")
                // Login y listado publico de posts quedan libres.
                .antMatchers("/", "/loginForm", "/processlogin", "/post", "/css/**", "/js/**", "/webfonts/**")
                    .permitAll()
                .anyRequest().permitAll();

        return http.build();
    }

    @Bean
    public UserDetailsService userDetailsService(DataSource dataSource) {
        JdbcUserDetailsManager manager = new JdbcUserDetailsManager(dataSource);
        manager.setUsersByUsernameQuery(
                "SELECT email, password, active FROM `user` WHERE email = ?");
        manager.setAuthoritiesByUsernameQuery(
                "SELECT u.email, r.role_name FROM `user` u " +
                "INNER JOIN role r ON u.id_role = r.id_role " +
                "WHERE u.email = ? AND u.active = 1");
        return manager;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
