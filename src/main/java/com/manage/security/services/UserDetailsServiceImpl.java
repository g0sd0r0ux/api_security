package com.manage.security.services;

import java.util.Collection;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.manage.security.models.UserModel;
import com.manage.security.repositories.UserRepository;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<UserModel> userOptional = userRepository.findByUsername(username);

        if(userOptional.isEmpty()) {
            throw new UsernameNotFoundException("The user could not be found");
        }

        UserModel userDB = userOptional.get();
        Collection<GrantedAuthority> authorities = userDB.getRoles().stream()
            .map(roleUser -> new SimpleGrantedAuthority(roleUser.getName()))
            .collect(Collectors.toSet());

        return new User(username, userDB.getPassword(), true, true, true, true, authorities);
    }

}
