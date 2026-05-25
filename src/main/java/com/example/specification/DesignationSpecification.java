package com.example.specification;

import org.springframework.data.jpa.domain.Specification;

import com.example.entity.Designation;

public class DesignationSpecification {
    
    public static Specification<Designation> searchDesignation(String keyword){

        return (root, query, cb) -> {
            if(keyword == null || keyword.isBlank()){
                return cb.conjunction();
            }

            String pattern = "%" + keyword.toLowerCase() + "%";

            return cb.or(

                    cb.like(
                            cb.lower(
                                    root.get("designationName")
                            ),
                            pattern
                    ),

                    cb.like(
                            cb.lower(
                                    root.get("designationCode")
                            ),
                            pattern
                    )
            );
        };
    }
}
