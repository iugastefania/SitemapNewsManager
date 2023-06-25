package com.ac.upt.sitemapnewsmanager.services;

import com.ac.upt.sitemapnewsmanager.models.Role;
import com.ac.upt.sitemapnewsmanager.models.User;
import com.ac.upt.sitemapnewsmanager.repositories.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import static org.mockito.Mockito.when;

@SpringBootTest
public class UserDetailsServiceImplementationTest {

    @Autowired
    UserDetailsServiceImplementation userDetailsService;

    @MockBean
    UserRepository userRepository;

    private final User user = new User("mock", "mockMail", "mockPassword", Role.ADMINISTRATOR);

    @Test
    public void loadUserByUsername_ExistingUser_ReturnsUserDetails() {
        // given
        when(userRepository.findByUsername("mock")).thenReturn(Optional.of(user));
        UserDetails expected = UserDetailsImplementation.build(user);

        // when
        UserDetails result = userDetailsService.loadUserByUsername("mock");

        // then
        assertEquals(expected, result);
    }

    @Test
    public void loadUserByUsername_NonexistentUser_ThrowsUsernameNotFoundException() {
        // given
        when(userRepository.findByUsername("nonexistent")).thenReturn(Optional.empty());

        // when/then
        assertThrows(
                UsernameNotFoundException.class,
                () -> userDetailsService.loadUserByUsername("nonexistent"));
    }
}

