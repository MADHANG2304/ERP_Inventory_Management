package com.example.specification;

import org.springframework.data.jpa.domain.Specification;

import com.example.entity.ApprovalConfig;

public class ApprovalConfigSpecification {

    public static Specification<ApprovalConfig> searchConfig(
            String keyword
    ) {

        return (root, query, criteriaBuilder) -> {

            if(keyword == null || keyword.isBlank()) {

                return criteriaBuilder.conjunction();
            }

            String pattern =
                    "%" + keyword.toLowerCase() + "%";

            return criteriaBuilder.or(

                    criteriaBuilder.like(
                            criteriaBuilder.lower(
                                    root.get("configName")
                            ),
                            pattern
                    ),

                    criteriaBuilder.like(
                            criteriaBuilder.lower(
                                    root.get("requestType")
                                            .as(String.class)
                            ),
                            pattern
                    )
            );
        };
    }
}