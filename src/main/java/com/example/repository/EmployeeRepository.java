package com.example.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import com.example.entity.Employee;

public interface EmployeeRepository extends JpaRepository<Employee, Long>, JpaSpecificationExecutor<Employee>{

    Employee findByEmail(String authenticatedUser);

    Employee findByUsername(String username);

    Employee findByRoleRoleName(
            String roleName
    );

    List<Employee> findByDepartment_DepartmentIdAndRole_RoleNameAndIsActive(
            Long departmentId,
            String roleName,
            Boolean isActive
    );
}
