package com.example.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import com.example.entity.Employee;

public interface EmployeeRepository extends JpaRepository<Employee, Long>, JpaSpecificationExecutor<Employee>{

    Employee findByEmail(String authenticatedUser);

    Employee findByUsername(String username);
    
    @Query("""
        SELECT e
        FROM Employee e
        WHERE e.department.departmentId = :departmentId
        AND e.role.roleName = :roleName

    """)
    Employee findManagerByDepartmentAndRole(
            Long departmentId,
            String roleName
    );

    @Query("""
        SELECT e
        FROM Employee e
        WHERE e.role.roleName = :roleName

    """)
    Employee findByUserRoleRoleName(
            String roleName
    );

    List<Employee> findByDepartment_DepartmentIdAndRole_RoleNameAndIsActive(
            Long departmentId,
            String roleName,
            Boolean isActive
    );
}
