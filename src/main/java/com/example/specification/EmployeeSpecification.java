package com.example.specification;

import org.springframework.data.jpa.domain.Specification;

import com.example.entity.Employee;

public class EmployeeSpecification {

    public static Specification<Employee> searchEmployee(String keyword) {

        return (root, query, cb) -> {

            if(keyword == null || keyword.isBlank()) {
                return cb.conjunction();
            }

            String pattern = "%" + keyword.toLowerCase() + "%";

            return cb.or(cb.like(cb.lower(root.get("employeeName")),pattern),

                    cb.like(cb.lower(root.get("email")), pattern)
            );
        };
    }

        public static Specification<Employee> hasUsername(String username) {
                return (root, query, cb) -> cb.equal(root.get("username"),username);
        };
}