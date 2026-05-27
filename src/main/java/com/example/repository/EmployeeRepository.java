package com.example.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import com.example.entity.Employee;

public interface EmployeeRepository extends JpaRepository<Employee, Long>, JpaSpecificationExecutor<Employee>{

    Employee findByEmail(String authenticatedUser);
    
    @Query("""
        SELECT e
        FROM Employee e
        WHERE e.department.departmentId = :departmentId
        AND e.user.roles.roleName = :roleName

    """)
    Employee findManagerByDepartmentAndRole(
            Long departmentId,
            String roleName
    );

    @Query("""
        SELECT e
        FROM Employee e
        WHERE e.user.roles.roleName = :roleName

    """)
    Employee findByUserRoleRoleName(
            String roleName
    );
}
