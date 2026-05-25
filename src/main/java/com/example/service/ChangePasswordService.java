package com.example.service;

import com.example.dto.ChangePasswordDTO;
import com.example.entity.User;
import com.example.repository.UserRepository;
import com.example.specification.UserSpecification;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class ChangePasswordService {

    private final UserRepository
            userRepository;

    private final PasswordEncoder
            passwordEncoder;

    public ChangePasswordService(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder
    ) {

        this.userRepository =
                userRepository;

        this.passwordEncoder =
                passwordEncoder;
    }

    public void changePassword(

            String username,

            ChangePasswordDTO dto
    ) {

        Specification<User> specification =

                UserSpecification
                        .hasUsername(username);

        User user =
                userRepository
                        .findOne(specification)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "User not found"
                                )
                        );

        if(!passwordEncoder.matches(

                dto.getOldPassword(),

                user.getPassword()
        )) {

            throw new RuntimeException(
                    "Old password is incorrect"
            );
        }

        if(!dto.getNewPassword()
                .equals(
                        dto.getConfirmPassword()
                )) {

            throw new RuntimeException(
                    "Password mismatch"
            );
        }

        if(dto.getNewPassword()
                .length() < 6) {

            throw new RuntimeException(
                    "Password must contain minimum 6 characters"
            );
        }

        user.setPassword(

                passwordEncoder.encode(
                        dto.getNewPassword()
                )
        );

        userRepository.save(user);
    }
}