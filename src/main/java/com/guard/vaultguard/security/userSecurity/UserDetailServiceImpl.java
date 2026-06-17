package com.guard.vaultguard.security.userSecurity;

import com.guard.vaultguard.entities.Users;
import com.guard.vaultguard.repositories.UserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class UserDetailServiceImpl implements UserDetailsService {

    private final UserRepository userRepo;

    public UserDetailServiceImpl (UserRepository repo) {
        this.userRepo = repo;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        Users user = userRepo.findByUsername(username).orElseThrow(
                () -> new UsernameNotFoundException("Username not Found")
        );

        return new UserPrinciple(user);
    }
}
