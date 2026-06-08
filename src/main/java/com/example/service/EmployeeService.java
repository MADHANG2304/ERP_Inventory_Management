package com.example.service;

import java.util.List;
import java.util.ArrayList;
import java.util.stream.Collectors;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.example.dto.DepartmentDTO;
import com.example.dto.DesignationDTO;
import com.example.dto.EmployeeDTO;
import com.example.dto.RoleDTO;
import com.example.entity.Department;
import com.example.entity.Designation;
import com.example.entity.Employee;
import com.example.entity.Roles;
import com.example.repository.DepartmentRepository;
import com.example.repository.DesignationRepository;
import com.example.repository.EmployeeRepository;
import com.example.repository.RoleRepository;
import com.example.specification.EmployeeSpecification;

@Service
public class EmployeeService {

    private final EmployeeRepository employeeRepository;

    private final DepartmentRepository departmentRepository;

    private final DesignationRepository designationRepository;

    private final RoleRepository roleRepository;

    private final PasswordEncoder passwordEncoder;

    public EmployeeService(EmployeeRepository employeeRepository, DepartmentRepository departmentRepository,
                        DesignationRepository designationRepository, RoleRepository roleRepository, PasswordEncoder passwordEncoder) {
        this.employeeRepository = employeeRepository;
        this.departmentRepository = departmentRepository;
        this.designationRepository = designationRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public EmployeeDTO saveEmployee(EmployeeDTO dto) {

        validateEmployee(dto);

        Employee employee;

        boolean isNewEmployee =
                dto.getEmployeeId() == null;

        if(isNewEmployee) {

            employee = new Employee();

        } else {

            employee = employeeRepository
                    .findById(dto.getEmployeeId())
                    .orElse(new Employee());
        }

        Department department =
                departmentRepository
                        .findById(dto.getDepartmentId())
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Department not found"
                                ));

        Designation designation =
                designationRepository
                        .findById(dto.getDesignationId())
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Designation not found"
                                ));

        Roles role =
                roleRepository
                        .findById(dto.getRoleId())
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Role not found"
                                ));

        employee.setDepartment(department);

        employee.setDesignation(designation);

        employee.setRole(role);

        employee.setEmployeeName(
                dto.getEmployeeName().trim()
        );

        employee.setMobileNumber(
                dto.getMobileNumber()
        );

        employee.setEmail(
                dto.getEmail().trim().toLowerCase()
        );

        employee.setUsername(
                dto.getEmail().trim().toLowerCase()
        );

        employee.setGender(
                dto.getGender()
        );

        employee.setState(
                dto.getState()
        );

        employee.setCity(
                dto.getCity()
        );

        employee.setIsActive(
                dto.getIsActive()
        );

        String generatedPassword = null;

        if(isNewEmployee) {

            generatedPassword =
                    generatePassword(
                            dto.getEmployeeName()
                    );

            employee.setPassword(
                    passwordEncoder.encode(
                            generatedPassword
                    )
            );
        }

        Employee savedEmployee =
                employeeRepository.save(employee);

        EmployeeDTO response =
                convertToDTO(savedEmployee);

        response.setGeneratedPassword(
                generatedPassword
        );

        return response;
    }

    public List<EmployeeDTO> getAllEmployees() {

        return employeeRepository
                .findAll()
                .stream()
                .map(this::convertToDTO)
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

    public List<EmployeeDTO> searchEmployees(String keyword) {

        Specification<Employee> specification = EmployeeSpecification.searchEmployee(keyword);

        return employeeRepository
                .findAll(specification)
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public void deleteEmployee(Long employeeId) {
        employeeRepository.deleteById(employeeId);
    }

    public List<DepartmentDTO> getAllDepartments() {

        return departmentRepository
                .findAll()
                .stream()
                .map(department -> {

                    DepartmentDTO dto = new DepartmentDTO();

                    dto.setDepartmentId(department.getDepartmentId());

                    dto.setDepartmentName(department.getDepartmentName());

                    return dto;
                })
                .collect(Collectors.toList());
    }

    public List<DesignationDTO> getAllDesignations() {

        return designationRepository
                .findAll()
                .stream()
                .map(designation -> {

                    DesignationDTO dto = new DesignationDTO();

                    dto.setDesignationId(designation.getDesignationId());

                    dto.setDesignationName(designation.getDesignationName());

                    return dto;
                })
                .collect(Collectors.toList());
    }

    private void validateEmployee(EmployeeDTO dto) {

        if(dto.getDepartmentId() == null) {
            throw new RuntimeException("Department is required");
        }

        if(dto.getDesignationId() == null) {
            throw new RuntimeException("Designation is required");
        }

        if(dto.getEmployeeName() == null || dto.getEmployeeName().isBlank()) {

            throw new RuntimeException("Employee name is required");
        }

        if(dto.getEmail() == null || dto.getEmail().isBlank()) {
            throw new RuntimeException("Email is required");
        }

        if(dto.getMobileNumber() == null || dto.getMobileNumber().isBlank()) {
            throw new RuntimeException("Mobile number is required");
        }

        if(dto.getRoleId() == null) {
            throw new RuntimeException(
                    "Role is required"
            );
        }

        boolean duplicateUsername =
                employeeRepository
                        .findAll()
                        .stream()
                        .anyMatch(employee ->

                                employee.getUsername() != null &&

                                employee.getUsername()
                                        .equalsIgnoreCase(
                                                dto.getEmail()
                                        )

                                &&

                                !employee.getEmployeeId()
                                        .equals(
                                                dto.getEmployeeId()
                                        )
                        );

        if(duplicateUsername) {

            throw new RuntimeException(
                    "Username already exists"
            );
        }

        boolean duplicateEmail =
                employeeRepository
                        .findAll()
                        .stream()
                        .anyMatch(employee ->
                                employee.getEmail().equalsIgnoreCase(dto.getEmail()) && 
                                !employee.getEmployeeId().equals(dto.getEmployeeId())
                        );

        if(duplicateEmail) {
            throw new RuntimeException("Email already exists");
        }

        boolean duplicateMobile =
                employeeRepository
                        .findAll()
                        .stream()
                        .anyMatch(employee ->
                                employee.getMobileNumber().equalsIgnoreCase(dto.getMobileNumber()) &&
                                !employee.getEmployeeId().equals(dto.getEmployeeId())
                        );

        if(duplicateMobile) {
            throw new RuntimeException("Mobile number already exists");
        }
    }

    private String generatePassword(
            String employeeName
    ) {

        String cleanName =
                employeeName.replaceAll(
                        "\\s+",
                        ""
                );

        return cleanName + "@123";
    }


        public void assignManager(
                Long employeeId,
                Long managerId
        ) {

                Employee employee =
                        employeeRepository
                                .findById(employeeId)
                                .orElseThrow(() ->
                                        new RuntimeException(
                                                "Employee not found"
                                        )
                                );

                Employee manager =
                        employeeRepository
                                .findById(managerId)
                                .orElseThrow(() ->
                                        new RuntimeException(
                                                "Manager not found"
                                        )
                                );

                if(employee.getEmployeeId().equals(manager.getEmployeeId())) {

                        throw new RuntimeException(
                                "Employee cannot be assigned as their own manager"
                        );
                }

                if(!"MANAGER".equals(
                        manager.getRole().getRoleName()
                )) {

                        throw new RuntimeException(
                                "Selected employee is not a manager"
                        );
                }

                if(!employee.getDepartment()
                        .getDepartmentId()
                        .equals(
                                manager.getDepartment()
                                        .getDepartmentId()
                        )) {

                        throw new RuntimeException(
                                "Manager must belong to same department"
                        );
                }

                employee.setManager(manager);

                employeeRepository.save(employee);
        }

        public List<EmployeeDTO> getManagersByDepartment(
                Long departmentId,
                Long employeeId
        ) {

                Employee employee =
                        employeeRepository
                                .findById(employeeId)
                                .orElseThrow(() ->
                                        new RuntimeException(
                                                "Employee not found"
                                        )
                                );

                List<Long> subordinateIds =
                        getSubordinateIds(employee);

                return employeeRepository
                        .findByDepartment_DepartmentIdAndRole_RoleNameAndIsActive(
                                departmentId,
                                "MANAGER",
                                true
                        )
                        .stream()

                        .filter(manager ->

                                !manager.getEmployeeId()
                                        .equals(employeeId)

                                &&

                                !subordinateIds.contains(
                                        manager.getEmployeeId()
                                )
                        )

                        .map(this::convertToDTO)

                        .toList();
        }

        private List<Long> getSubordinateIds(
                Employee employee
        ) {

                List<Long> subordinateIds = new ArrayList<>();

                collectSubordinates(
                        employee,
                        subordinateIds
                );

                return subordinateIds;
        }

        private void collectSubordinates(
                Employee manager,
                List<Long> subordinateIds
        ) {

                employeeRepository
                        .findAll()
                        .stream()

                        .filter(employee ->

                                employee.getManager() != null

                                &&

                                employee.getManager()
                                        .getEmployeeId()
                                        .equals(manager.getEmployeeId())
                        )

                        .forEach(subordinate -> {

                                subordinateIds.add(
                                        subordinate.getEmployeeId()
                                );

                                collectSubordinates(
                                        subordinate,
                                        subordinateIds
                                );
                });
        }

        private EmployeeDTO convertToDTO(Employee employee) {

                EmployeeDTO dto = new EmployeeDTO();

                dto.setEmployeeId(employee.getEmployeeId());

                dto.setDepartmentId(employee.getDepartment().getDepartmentId());

                dto.setDepartmentName(employee.getDepartment().getDepartmentName());

                dto.setDesignationId(employee.getDesignation().getDesignationId());

                dto.setDesignationName(employee.getDesignation().getDesignationName());

                dto.setEmployeeName(employee.getEmployeeName());

                dto.setMobileNumber(employee.getMobileNumber());

                dto.setEmail(employee.getEmail());

                dto.setGender(employee.getGender());

                dto.setState(employee.getState());

                dto.setCity(employee.getCity());

                dto.setIsActive(employee.getIsActive());

                if(employee.getRole() != null) {

                        dto.setRoleId(employee.getRole().getRoleId());

                        dto.setRoleName(employee.getRole().getRoleName());
                }

                if(employee.getManager() != null) {

                        dto.setManagerEmployeeId(
                                employee.getManager().getEmployeeId()
                        );

                        dto.setManagerEmployeeName(
                                employee.getManager().getEmployeeName()
                        );
                }

                dto.setUsername(employee.getUsername());

                return dto;
        }
}