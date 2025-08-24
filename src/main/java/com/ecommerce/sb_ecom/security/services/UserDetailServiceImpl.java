package com.ecommerce.sb_ecom.security.services;

import com.ecommerce.sb_ecom.Repository.UserRepositiory;
import com.ecommerce.sb_ecom.model.User;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class UserDetailServiceImpl implements UserDetailsService {
    @Autowired
    UserRepositiory userRepositiory;
    @Override
    @Transactional
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepositiory.findByUserName(username).
                orElseThrow(()-> new UsernameNotFoundException("user not found with username"+ username));
        return UserDetailsImpl.build(user);
    }
}
