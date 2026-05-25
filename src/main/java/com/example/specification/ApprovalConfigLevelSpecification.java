package com.example.specification;

import com.example.entity.ApprovalConfigLevel;
import org.springframework.data.jpa.domain.Specification;

public class ApprovalConfigLevelSpecification {

    public static Specification<ApprovalConfigLevel>
    hasConfigId(
            Long configId
    ) {

        return (root, query, criteriaBuilder) ->

                criteriaBuilder.equal(

                        root.get("approvalConfig")
                                .get("configId"),

                        configId
                );
    }
}