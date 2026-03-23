package com.example.bai4.service;

import com.example.bai4.entity.Account;
import com.example.bai4.repo.AccountRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.*;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.stream.Collectors;

@Service
public class AccountService implements UserDetailsService {

    @Autowired
    private AccountRepository accountRepository;

    @Override
    public UserDetails loadUserByUsername(String username)
            throws UsernameNotFoundException {

        Account account = accountRepository
                .findByLoginName(username)
                .orElseThrow(() ->
                        new UsernameNotFoundException("Could not find user"));

        Set<SimpleGrantedAuthority> authorities =
                account.getRoles()
                        .stream()
                        .map(role -> new SimpleGrantedAuthority(role.getName()))
                        .collect(Collectors.toSet());

        return new org.springframework.security.core.userdetails.User(
                account.getLoginName(),
                account.getPassword(),
                authorities
        );
    }
}