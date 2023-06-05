package com.ac.upt.sitemapnewsmanager.services;

import com.ac.upt.sitemapnewsmanager.models.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.ac.upt.sitemapnewsmanager.repositories.UserRepository;
import java.util.Optional;

@Service
public class UserDetailService implements UserDetailsService {
    @Autowired
    UserRepository userRepository;

    @Override
    @Transactional
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<User> user = userRepository.findByUsername(username);
        return user.map(UserDetail::build)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with username: " + username));
    }
}