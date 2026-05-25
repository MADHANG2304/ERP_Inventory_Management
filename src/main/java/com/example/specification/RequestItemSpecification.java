package com.example.specification;

import org.springframework.data.jpa.domain.Specification;

import com.example.entity.RequestItems;

public class RequestItemSpecification {

    public static Specification<RequestItems>
    hasRequestId(Long requestId){

        return (root, query, criteriaBuilder) ->

                criteriaBuilder.equal(
                        root.get("request")
                                .get("requestId"),
                        requestId
                );
    }
}