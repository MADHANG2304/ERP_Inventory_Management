package com.example.specification;

import org.springframework.data.jpa.domain.Specification;

import com.example.entity.Department;

public class DepartmentSpecification {
    
    public static Specification<Department> searchDepartment(String keyword){
        return(root, query, cb) -> {
            if(keyword == null || keyword.isBlank()){
                return cb.conjunction();
            }

            String pattern = "%" + keyword.toLowerCase() + "%";

            return cb.or(
                cb.like(
                    cb.lower(
                        root.get("departmentName")),pattern
                ),

                cb.like(
                    cb.lower(
                        root.get("departmentCode")), pattern
                )
            );
        };
    }
}
