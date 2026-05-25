package com.example.specification;

import org.springframework.data.jpa.domain.Specification;

import com.example.entity.RequestApproval;
import com.example.enums.ApprovalRole;
import com.example.enums.ApprovalStatus;

public class ApprovalSpecification {

    public static Specification<RequestApproval>
    hasRequestNumber(
            String requestNumber
    ) {

        return (root, query, criteriaBuilder) -> {

            if(requestNumber == null
                    || requestNumber.isBlank()) {

                return criteriaBuilder.conjunction();
            }

            return criteriaBuilder.like(

                    criteriaBuilder.lower(

                            root.get("request")
                                    .get("requestNumber")
                    ),

                    "%" +
                            requestNumber.toLowerCase()
                            + "%"
            );
        };
    }

    public static Specification<RequestApproval>
    hasApprovalStatus(
            ApprovalStatus approvalStatus
    ) {

        return (root, query, criteriaBuilder) -> {

            if(approvalStatus == null) {

                return criteriaBuilder.conjunction();
            }

            return criteriaBuilder.equal(

                    root.get("approvalStatus"),
                    approvalStatus
            );
        };
    }

    public static Specification<RequestApproval>
    hasApprovalRole(
            ApprovalRole approvalRole
    ) {

        return (root, query, criteriaBuilder) -> {

            if(approvalRole == null) {

                return criteriaBuilder.conjunction();
            }

            return criteriaBuilder.equal(

                    root.get("approvalRole"),
                    approvalRole
            );
        };
    }

    public static Specification<RequestApproval>
    hasCurrentLevel(
            Boolean currentLevel
    ) {

        return (root, query, criteriaBuilder) -> {

            if(currentLevel == null) {

                return criteriaBuilder.conjunction();
            }

            return criteriaBuilder.equal(

                    root.get("isCurrentLevel"),
                    currentLevel
            );
        };
    }

    
}
