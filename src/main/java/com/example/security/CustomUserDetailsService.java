package com.example.security;

import java.util.Collections;

import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Service;

import com.example.entity.Employee;
import com.example.repository.EmployeeRepository;

@Service
public class CustomUserDetailsService implements UserDetailsService{
    private final EmployeeRepository employeeRepository;

    public CustomUserDetailsService(EmployeeRepository employeeRepository){
        this.employeeRepository = employeeRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Employee employee = employeeRepository
                .findAll()
                .stream()
                .filter(u -> u.getUsername().equals(username))
                .findFirst()
                .orElseThrow(() -> new UsernameNotFoundException("Invalid Username"));

        if(Boolean.FALSE.equals(employee.getIsActive())) {
                throw new UsernameNotFoundException(
                        "Your account is inactive. Please contact administrator."
                );
        }

        return new User(
            employee.getUsername(),
            employee.getPassword(),
            Collections.singleton(new SimpleGrantedAuthority("ROLE_" + employee.getRole().getRoleName()))
        );
    }



    
}
