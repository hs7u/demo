package com.batch.demo.security;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import com.batch.demo.vo.vo.Role;

public final class RoleHelper {
    private static final String ROLE_PREFIX = "ROLE_";
    
    public static Collection<? extends GrantedAuthority> castToGrantedAuthorities(Role... roles){
        List<SimpleGrantedAuthority> authorities = new ArrayList<>();
        Arrays.stream(roles).forEach(role -> {
            authorities.add(new SimpleGrantedAuthority(String.format("%s%s",ROLE_PREFIX,role.getRoleName())));
        });
    
        return authorities;
    }
    
}
