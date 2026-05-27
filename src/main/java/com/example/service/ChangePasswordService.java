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

    private final AuditLogService auditLogService;

    public ChangePasswordService(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder,
            AuditLogService auditLogService
    ) {

        this.userRepository =
                userRepository;

        this.passwordEncoder =
                passwordEncoder;

        this.auditLogService = 
                auditLogService;
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

        String password = dto.getNewPassword();

        String passwordPattern =
                "^(?=.*[0-9])" +          // at least one number
                "(?=.*[a-z])" +          // at least one small letter
                "(?=.*[A-Z])" +          // at least one capital letter
                "(?=.*[@#$%^&+=!])" +   // at least one special character
                "(?=\\S+$)" +           // no spaces
                ".{6,}$";               // minimum 6 characters

        if(!password.matches(passwordPattern)) {

        throw new RuntimeException(

                "Password must contain:\n" +

                "• One uppercase letter\n" +

                "• One lowercase letter\n" +

                "• One number\n" +

                "• One special character\n" +

                "• Minimum 6 characters"
        );
        }

        user.setPassword(

                passwordEncoder.encode(
                        dto.getNewPassword()
                )
        );

        userRepository.save(user);

        auditLogService.logAction(

                "USER_MODULE",

                "PASSWORD_CHANGE",

                "Password changed for : "
                        + user.getUsername()
        );
    }
}