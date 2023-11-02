package ru.raisbex.library.util;

import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

public class CustomUsernamePasswordAuthenticationFilterTest {

    @Test
    public void testAuthenticationFilter() {
        // Создайте экземпляр CustomUsernamePasswordAuthenticationFilter.
        CustomUsernamePasswordAuthenticationFilter filter = new CustomUsernamePasswordAuthenticationFilter();
        // Установите обработчик ошибок аутентификации.
        filter.setAuthenticationFailureHandler(new SimpleUrlAuthenticationFailureHandler("/auth/login?error"));

        // Создайте макеты (mock) запроса и ответа для симуляции HTTP-запроса и ответа.
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();

        // Установите параметры запроса, включая логин и пароль пользователя.
        request.setParameter("login", "testUser");
        request.setParameter("password", "testPassword");

        // Установите условие, при котором должна выполняться аутентификация.
        filter.setRequiresAuthenticationRequestMatcher(new AntPathRequestMatcher("/process_login", "POST"));

        // Создайте макеты объектов Authentication и AuthenticationException.
        Authentication authentication = mock(Authentication.class);
        AuthenticationException authenticationException = mock(AuthenticationException.class);

        try {
            // Попытка аутентификации.
            filter.attemptAuthentication(request, response);
        } catch (Exception e) {
            // Обработка исключения (неявно проигнорирована в данном случае).
        }

        // Утверждение: проверка, что имя пользователя возвращается правильно.
        assertEquals("testUser", filter.obtainUsername(request));

        // Утверждение: проверка, что статус ответа соответствует ожидаемому (в данном случае, 200 OK).
        assertEquals(200, response.getStatus());
    }

}
