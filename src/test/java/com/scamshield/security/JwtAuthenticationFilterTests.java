
package com.scamshield.security;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.mock.web.MockFilterChain;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;

@ExtendWith(MockitoExtension.class)
class JwtAuthenticationFilterTests {

    @Mock
    private JwtService jwtService;

    @Mock
    private UserDetailsService userDetailsService;

    private JwtAuthenticationFilter filter;

    @BeforeEach
    void setUp() {
        filter = new JwtAuthenticationFilter(
                jwtService, userDetailsService);

        SecurityContextHolder.clearContext();
    }

    @AfterEach
    void cleanUp() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void missingTokenShouldContinueFilterChain() throws Exception {

        MockHttpServletRequest request =
                new MockHttpServletRequest();

        MockHttpServletResponse response =
                new MockHttpServletResponse();

        MockFilterChain chain = new MockFilterChain();

        filter.doFilterInternal(request, response, chain);

        assertNotNull(chain.getRequest());
        assertNull(
                SecurityContextHolder.getContext().getAuthentication());

        verifyNoInteractions(jwtService, userDetailsService);
    }

    @Test
    void invalidTokenShouldClearSecurityContext() throws Exception {

        MockHttpServletRequest request =
                new MockHttpServletRequest();

        request.addHeader("Authorization", "Bearer invalid-token");

        MockHttpServletResponse response =
                new MockHttpServletResponse();

        MockFilterChain chain = new MockFilterChain();

        when(jwtService.extractEmail("invalid-token"))
                .thenThrow(new IllegalArgumentException("Invalid token"));

        filter.doFilterInternal(request, response, chain);

        assertNotNull(chain.getRequest());
        assertNull(
                SecurityContextHolder.getContext().getAuthentication());

        verify(jwtService).extractEmail("invalid-token");
        verifyNoInteractions(userDetailsService);
    }

    @Test
    void validTokenShouldAuthenticateUser() throws Exception {

        MockHttpServletRequest request =
                new MockHttpServletRequest();

        request.addHeader("Authorization", "Bearer valid-token");

        MockHttpServletResponse response =
                new MockHttpServletResponse();

        MockFilterChain chain = new MockFilterChain();

        UserDetails userDetails = User
                .withUsername("usera@test.com")
                .password("encoded-password")
                .roles("RESIDENT")
                .build();

        when(jwtService.extractEmail("valid-token"))
                .thenReturn("usera@test.com");

        when(userDetailsService.loadUserByUsername("usera@test.com"))
                .thenReturn(userDetails);

        when(jwtService.isTokenValid(
                "valid-token", "usera@test.com"))
                .thenReturn(true);

        filter.doFilterInternal(request, response, chain);

        Authentication authentication =
                SecurityContextHolder.getContext().getAuthentication();

        assertNotNull(authentication);
        assertEquals("usera@test.com", authentication.getName());
        assertEquals(true, authentication.isAuthenticated());
        assertNotNull(chain.getRequest());

        verify(userDetailsService)
                .loadUserByUsername("usera@test.com");
    }

    @Test
    void tokenWithWrongEmailShouldNotAuthenticateUser()
            throws Exception {

        MockHttpServletRequest request =
                new MockHttpServletRequest();

        request.addHeader("Authorization", "Bearer valid-token");

        MockHttpServletResponse response =
                new MockHttpServletResponse();

        MockFilterChain chain = new MockFilterChain();

        UserDetails userDetails = User
                .withUsername("usera@test.com")
                .password("encoded-password")
                .roles("RESIDENT")
                .build();

        when(jwtService.extractEmail("valid-token"))
                .thenReturn("usera@test.com");

        when(userDetailsService.loadUserByUsername("usera@test.com"))
                .thenReturn(userDetails);

        when(jwtService.isTokenValid(
                "valid-token", "usera@test.com"))
                .thenReturn(false);

        filter.doFilterInternal(request, response, chain);

        assertNull(
                SecurityContextHolder.getContext().getAuthentication());

        assertNotNull(chain.getRequest());
    }
}
