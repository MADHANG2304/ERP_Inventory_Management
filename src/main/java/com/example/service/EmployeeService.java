package com.example.service;

import com.example.dto.DepartmentDTO;
import com.example.dto.DesignationDTO;
import com.example.dto.EmployeeDTO;
import com.example.entity.Department;
import com.example.entity.Designation;
import com.example.entity.Employee;
import com.example.repository.DepartmentRepository;
import com.example.repository.DesignationRepository;
import com.example.repository.EmployeeRepository;
import com.example.specification.EmployeeSpecification;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class EmployeeService {

    private final EmployeeRepository employeeRepository;

    private final DepartmentRepository departmentRepository;

    private final DesignationRepository designationRepository;

    public EmployeeService(EmployeeRepository employeeRepository, DepartmentRepository departmentRepository, DesignationRepository designationRepository) {
        this.employeeRepository = employeeRepository;
        this.departmentRepository = departmentRepository;
        this.designationRepository = designationRepository;
    }

    public EmployeeDTO saveEmployee(EmployeeDTO dto) {

        validateEmployee(dto);

        Employee employee;

        if(dto.getEmployeeId() != null) {
            employee = employeeRepository
                    .findById(dto.getEmployeeId())
                    .orElse(new Employee());

        } else {
            employee = new Employee();
        }

        Department department =
                departmentRepository
                        .findById(dto.getDepartmentId())
                        .orElseThrow(() -> new RuntimeException("Department not found"));

        Designation designation =
                designationRepository
                        .findById(dto.getDesignationId())
                        .orElseThrow(() -> new RuntimeException("Designation not found"));

        employee.setDepartment(department);

        employee.setDesignation(designation);

        employee.setEmployeeName(dto.getEmployeeName().trim());

        employee.setMobileNumber(dto.getMobileNumber());

        employee.setEmail(dto.getEmail().trim().toLowerCase());

        employee.setGender(dto.getGender());

        employee.setState(dto.getState());

        employee.setCity(dto.getCity());

        employee.setIsActive(dto.getIsActive());

        Employee savedEmployee = employeeRepository.save(employee);

        return convertToDTO(savedEmployee);
    }

    public List<EmployeeDTO> getAllEmployees() {

        return employeeRepository
                .findAll()
                .stream()
                .map(this::convertToDTO)
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

        return dto;
    }
}