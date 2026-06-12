package com.example.service;

import com.example.dto.ChangePasswordDTO;
import com.example.entity.Employee;
import com.example.repository.EmployeeRepository;
import com.example.specification.EmployeeSpecification;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class ChangePasswordService {

    private final EmployeeRepository
            employeeRepository;

    private final PasswordEncoder
            passwordEncoder;

    private final AuditLogService auditLogService;

    public ChangePasswordService(EmployeeRepository employeeRepository, PasswordEncoder passwordEncoder, AuditLogService auditLogService){
        this.employeeRepository = employeeRepository;
        this.passwordEncoder = passwordEncoder;
        this.auditLogService = auditLogService;
    }

    public void changePassword(String username, ChangePasswordDTO dto){

        Specification<Employee> specification = EmployeeSpecification.hasUsername(username);

        Employee employee = employeeRepository
                        .findOne(specification)
                        .orElseThrow(() ->
                                new RuntimeException("User not found")
                        );

        if(!passwordEncoder.matches(dto.getOldPassword(), employee.getPassword())){
            throw new RuntimeException("Old password is incorrect");
        }

        if(!dto.getNewPassword().equals(dto.getConfirmPassword())){
            throw new RuntimeException("Password mismatch");
        }

        String password = dto.getNewPassword();

        String passwordPattern = "^[a-zA-Z0-9._]+@[a-zA-Z]+\\.[a-zA-Z]{2,}$";

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

        employee.setPassword(
                passwordEncoder.encode(dto.getNewPassword())
        );

        employeeRepository.save(employee);

        auditLogService.logAction(

                "USER_MODULE",

                "PASSWORD_CHANGE",

                "Password changed for : " + employee.getUsername()
        );
    }
}