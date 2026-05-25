package com.example.specification;

import org.springframework.data.jpa.domain.Specification;

import com.example.entity.RequestApproval;

public class RequestApprovalSpecification {
    public static Specification<RequestApproval>
hasRequestId(
        Long requestId
) {

    return (root, query, criteriaBuilder) ->

            criteriaBuilder.equal(

                    root.get("request")
                            .get("requestId"),

                    requestId
            );
}
}
