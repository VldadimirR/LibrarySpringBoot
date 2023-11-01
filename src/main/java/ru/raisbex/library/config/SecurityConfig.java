package ru.raisbex.library.config;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import ru.raisbex.library.services.PeopleService;
import ru.raisbex.library.util.CustomUsernamePasswordAuthenticationFilter;


@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class SecurityConfig extends WebSecurityConfigurerAdapter implements WebMvcConfigurer {

    private final PeopleService personDetailsService;

    @Autowired
    public SecurityConfig(PeopleService personDetailsService) {
        this.personDetailsService = personDetailsService;
    }

    // Создание и настройка пользовательского фильтра аутентификации
    @Bean
    public CustomUsernamePasswordAuthenticationFilter customUsernamePasswordAuthenticationFilter() throws Exception {
        CustomUsernamePasswordAuthenticationFilter filter = new CustomUsernamePasswordAuthenticationFilter();
        filter.setAuthenticationManager(authenticationManagerBean());
        return filter;
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        // Конфигурация правил безопасности
        http.addFilterBefore(customUsernamePasswordAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class)
                .authorizeRequests()
                .antMatchers("/img/bookImg","/img/**","/css/**", "/js/**","/auth/login", "/auth/registration", "/error").permitAll() // Доступ без аутентификации к указанным страницам и ресурсам
                .antMatchers("/people","/books/edit", "/books/new", "/people/edit", "/people/new", "/people/index", "/people/show").hasRole("ADMIN") // Доступ для пользователей с ролью ADMIN к определенным страницам
                .antMatchers("/profile").authenticated() // Доступ к /profile только для авторизованных пользователей
                .anyRequest().hasAnyRole("USER", "ADMIN") // Доступ для пользователей с ролью USER и ADMIN ко всем остальным страницам
                .and()
                .formLogin()
                .loginPage("/auth/login") // Страница входа
                .loginProcessingUrl("/process_login")
                .defaultSuccessUrl("/books", true) // Перенаправление после успешной аутентификации
                .failureUrl("/auth/login?error") // Перенаправление после ошибки аутентификации
                .and()
                .logout()
                .logoutUrl("/logout") // URL для выхода
                .logoutSuccessUrl("/books"); // Перенаправление после успешного выхода
    }

    // Настройка аутентификации пользователей
    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(personDetailsService)
                .passwordEncoder(getPasswordEncoder());
    }

    // Bean для шифрования паролей
    @Bean
    public PasswordEncoder getPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Override
    public void addResourceHandlers(final ResourceHandlerRegistry registry) {
        // Конфигурация для обработки запросов к статическим ресурсам
        registry.addResourceHandler("/upload/**").addResourceLocations("file://" + System.getProperty("user.dir") + "/src/main/upload/");
    }
}

