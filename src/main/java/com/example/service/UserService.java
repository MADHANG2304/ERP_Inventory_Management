package com.example.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.example.dto.EmployeeDTO;
import com.example.dto.RoleDTO;
import com.example.dto.UserDTO;
import com.example.entity.Employee;
import com.example.entity.Roles;
import com.example.entity.User;
import com.example.repository.EmployeeRepository;
import com.example.repository.RoleRepository;
import com.example.repository.UserRepository;
import com.example.specification.UserSpecification;

@Service
public class UserService {

    private final UserRepository userRepository;

    private final EmployeeRepository employeeRepository;

    private final RoleRepository roleRepository;

    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository , EmployeeRepository employeeRepository,
                RoleRepository roleRepository , PasswordEncoder passwordEncoder) {

        this.userRepository = userRepository;
        this.employeeRepository = employeeRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public UserDTO saveUser(UserDTO dto) {

        validateUser(dto);

        User user;

        boolean isNewUser = dto.getUserId() == null;


        if(isNewUser) {
                user = new User();
        } 
        else {
                user = userRepository
                        .findById(dto.getUserId())
                        .orElseThrow(() -> new RuntimeException("User not found"));
        }

        Employee employee =
                employeeRepository
                        .findById(dto.getEmployeeId())
                        .orElseThrow(() -> new RuntimeException("Employee not found"));

        Roles role =
                roleRepository
                        .findById(dto.getRoleId())
                        .orElseThrow(() -> new RuntimeException("Role not found"));

                        
        user.setEmployee(employee);
        user.setRoles(role);
        user.setUsername(employee.getEmail().trim().toLowerCase());
        user.setEmail(employee.getEmail().trim().toLowerCase());
        user.setIsActive(dto.getIsActive());
        
        String generatedPassword = null;

        if(isNewUser) {
                generatedPassword = generatePassword(employee.getEmployeeName());

                user.setPassword(passwordEncoder.encode(generatedPassword));
        }
        User savedUser = userRepository.save(user);

        UserDTO response = convertToDTO(savedUser);

        response.setGeneratedPassword(generatedPassword);

        return response;
    }

    public List<UserDTO> getAllUsers() {

        return userRepository
                .findAll()
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public List<UserDTO> searchUsers(String keyword) {

        Specification<User> specification = UserSpecification.searchUsers(keyword);

        return userRepository
                .findAll(specification)
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public void deleteUser(Long userId) {
        userRepository.deleteById(userId);
    }

    public List<EmployeeDTO> getAvailableEmployees() {

        return employeeRepository
                .findAll()
                .stream()

                .filter(employee -> employee.getUser() == null)

                .map(employee -> {
                    EmployeeDTO dto = new EmployeeDTO();

                    dto.setEmployeeId(employee.getEmployeeId());

                    dto.setEmployeeName(employee.getEmployeeName());

                    dto.setEmail(employee.getEmail());

                    return dto;
                })
                .collect(Collectors.toList());
    }

    public List<RoleDTO> getAllRoles() {

        return roleRepository
                .findAll()
                .stream()
                .map(role -> {

                    RoleDTO dto = new RoleDTO();

                    dto.setRoleId(role.getRoleId());

                    dto.setRoleName(role.getRoleName());

                    return dto;
                })
                .collect(Collectors.toList());
    }

    private void validateUser(UserDTO dto) {

        if(dto.getEmployeeId() == null) {
            throw new RuntimeException("Employee is required");
        }

        if(dto.getRoleId() == null) {

            throw new RuntimeException("Role is required");
        }

        boolean employeeAlreadyMapped =
                userRepository
                        .findAll()
                        .stream()
                        .anyMatch(user -> user.getEmployee() != null &&
                                user.getEmployee().getEmployeeId().equals(dto.getEmployeeId()) &&
                                !user.getUserId().equals(dto.getUserId())
                        );

        if(employeeAlreadyMapped) {
            throw new RuntimeException("User already exists for employee");
        }
    }

    private String generatePassword(String employeeName) {

        String cleanName = employeeName.replaceAll("\\s+", "");

        // int randomNumber = new Random().nextInt(900) + 100;

        return cleanName + "@123";
    }

    private UserDTO convertToDTO(User user) {

        UserDTO dto = new UserDTO();

        dto.setUserId(user.getUserId());

        if(user.getEmployee() != null) {
                dto.setEmployeeId(user.getEmployee().getEmployeeId());
                
                dto.setEmployeeName(user.getEmployee().getEmployeeName());
                
                dto.setEmail(user.getEmployee().getEmail());
        }
        if(user.getRoles() != null) {
                dto.setRoleId(user.getRoles().getRoleId());
        
                dto.setRoleName(user.getRoles().getRoleName());
        }

        dto.setUsername(user.getUsername());
        
        dto.setIsActive(user.getIsActive());

        return dto;
    }
}