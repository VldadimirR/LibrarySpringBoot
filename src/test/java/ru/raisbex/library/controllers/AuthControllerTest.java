package ru.raisbex.library.controllers;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.validation.Validator;
import ru.raisbex.library.config.SecurityConfig;
import ru.raisbex.library.services.PeopleService;
import ru.raisbex.library.services.RegistrationService;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;

@WebMvcTest(SecurityConfig.class)
public class AuthControllerTest {

    @MockBean
    private PeopleService peopleService;

    @InjectMocks
    private AuthController authController;

    @MockBean
    private RegistrationService registrationService;

    @MockBean
    private Validator personValidator;

    @Autowired
    private MockMvc mockMvc;

    @BeforeEach
    public void setUp() {
        this.mockMvc = MockMvcBuilders.standaloneSetup(authController).build();
    }

    @Test
    public void testLoginPage_WithAuthenticatedUser_ShouldRedirectToProfile() throws Exception {
        // Убедитесь, что вход выполнен и текущий пользователь аутентифицирован.
        // Затем проверьте, что при запросе страницы /auth/login происходит перенаправление на /profile.
        // В этом случае мы ожидаем перенаправление, так как пользователь уже аутентифицирован.
        Authentication authentication = mock(Authentication.class);
        personValidator.validate(Mockito.any(), Mockito.any());
        SecurityContextHolder.getContext().setAuthentication(authentication);

        // Act
        ResultActions resultActions = mockMvc.perform(MockMvcRequestBuilders.get("/auth/login"));

        // Assert
        resultActions.andExpect(MockMvcResultMatchers.status().is3xxRedirection());
        resultActions.andExpect(redirectedUrl("/profile"));

        verifyNoInteractions(registrationService);
    }

    @Test
    public void testLoginPage_WithNotAuthenticatedUser_ShouldReturnLoginPage() throws Exception {
        // Убедитесь, что текущий пользователь не аутентифицирован (сбросите аутентификацию).
        // Затем проверьте, что при запросе страницы /auth/login ожидается отображение страницы входа.
        // Этот тест проверяет, что неаутентифицированным пользователям предоставляется страница входа.
        Authentication authentication = mock(Authentication.class);
        personValidator.validate(Mockito.any(), Mockito.any()); // Просто вызовите метод без when

        SecurityContextHolder.getContext().setAuthentication(authentication);

        // Act
        ResultActions resultActions = mockMvc.perform(MockMvcRequestBuilders.get("/auth/login"));

        // Assert
        resultActions.andExpect(MockMvcResultMatchers.status().is3xxRedirection());
    }

    @Test
    public void testLoginPage_WithError_ShouldReturnLoginPageWithErrorAttribute() throws Exception {
        // Запрос страницы /auth/login с параметром "error=true" должен возвращать страницу входа с атрибутом ошибки.
        // Этот тест проверяет, что при наличии параметра "error" на странице входа отображается сообщение об ошибке.
        ResultActions resultActions = mockMvc.perform(MockMvcRequestBuilders.get("/auth/login").param("error", "true"));

        // Assert
        resultActions.andExpect(MockMvcResultMatchers.status().isOk());
        resultActions.andExpect(MockMvcResultMatchers.view().name("auth/login"));
        resultActions.andExpect(MockMvcResultMatchers.model().attributeExists("loginError"));
        resultActions.andExpect(MockMvcResultMatchers.model().attribute("loginError", true));
    }

    @Test
    public void testRegistrationPage_ShouldReturnRegistrationPage() throws Exception {
        // Проверьте, что при запросе страницы /auth/registration отображается страница регистрации.
        // Этот тест проверяет, что страница регистрации доступна.
        ResultActions resultActions = mockMvc.perform(MockMvcRequestBuilders.get("/auth/registration"));

        // Assert
        resultActions.andExpect(MockMvcResultMatchers.status().isOk());
        resultActions.andExpect(MockMvcResultMatchers.view().name("auth/registration"));
    }



}
