package com.example.specification;

import com.example.entity.InventoryRequest;
import com.example.entity.RequestItems;
import com.example.enums.RequestStatus;

import java.time.LocalDate;
import java.time.LocalDateTime;

import org.springframework.data.jpa.domain.Specification;

public class InventoryRequestSpecification {

        public static Specification<InventoryRequest> searchRequest(
                        String keyword) {

                return (root, query, criteriaBuilder) -> {

                        if (keyword == null || keyword.isBlank()) {

                                return criteriaBuilder.conjunction();
                        }

                        String pattern = "%" + keyword.toLowerCase() + "%";

                        return criteriaBuilder.or(

                                        criteriaBuilder.like(
                                                        criteriaBuilder.lower(
                                                                        root.get("requestNumber")),
                                                        pattern),

                                        criteriaBuilder.like(
                                                        criteriaBuilder.lower(
                                                                        root.get("employee")
                                                                                        .get("employeeName")),
                                                        pattern),

                                        criteriaBuilder.like(
                                                        criteriaBuilder.lower(
                                                                        root.get("requestStatus")
                                                                                        .as(String.class)),
                                                        pattern));
                };
        }

        public static Specification<InventoryRequest>
        hasRequestId(
                Long requestId
        ) {

        return (root, query, criteriaBuilder) ->

                criteriaBuilder.equal(

                        root.get("requestId"),

                        requestId
                );
        }

        public static Specification<InventoryRequest>
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
                                        root.get("requestNumber")
                                ),

                                "%" +
                                        requestNumber.toLowerCase()
                                        + "%"
                        );
                };
        }

        public static Specification<InventoryRequest>
                hasEmployeeName(
                        String employeeName
                ) {

                return (root, query, criteriaBuilder) -> {

                        if(employeeName == null
                                || employeeName.isBlank()) {

                        return criteriaBuilder.conjunction();
                        }

                        return criteriaBuilder.like(

                                criteriaBuilder.lower(

                                        root.get("employee")
                                                .get("employeeName")
                                ),

                                "%" +
                                        employeeName.toLowerCase()
                                        + "%"
                        );
                };
        }

        public static Specification<InventoryRequest>
                hasStatus(
                        RequestStatus status
                ) {

                return (root, query, criteriaBuilder) -> {

                        if(status == null) {

                        return criteriaBuilder.conjunction();
                        }

                        return criteriaBuilder.equal(

                                root.get("requestStatus"),
                                status
                        );
                };
        }

        public static Specification<InventoryRequest>
                hasFromDate(
                        LocalDate fromDate
                ) {

                return (root, query, criteriaBuilder) -> {

                        if(fromDate == null) {

                        return criteriaBuilder.conjunction();
                        }

                        return criteriaBuilder.greaterThanOrEqualTo(

                                root.get("requestDate"),

                                fromDate.atStartOfDay()
                        );
                };
        }

        public static Specification<InventoryRequest>
                hasToDate(
                        LocalDate toDate
                ) {

                return (root, query, criteriaBuilder) -> {

                        if(toDate == null) {

                        return criteriaBuilder.conjunction();
                        }

                        return criteriaBuilder.lessThanOrEqualTo(

                                root.get("requestDate"),

                                toDate.atTime(23, 59, 59)
                        );
                };
        }

        public static Specification<InventoryRequest>
                hasEmployeeId(
                        Long employeeId
                ) {

                return (root, query, criteriaBuilder) -> {

                        if(employeeId == null) {

                        return criteriaBuilder.conjunction();
                        }

                        return criteriaBuilder.equal(

                                root.get("employee")
                                        .get("employeeId"),

                                employeeId
                        );
                };
        }

        
}