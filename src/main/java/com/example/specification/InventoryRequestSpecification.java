package com.example.specification;

import java.time.LocalDate;

import org.springframework.data.jpa.domain.Specification;

import com.example.entity.InventoryRequest;
import com.example.enums.RequestStatus;

public class InventoryRequestSpecification {

        public static Specification<InventoryRequest> searchRequest(String keyword)
        {

                return (root, query, cb) -> {

                        if (keyword == null || keyword.isBlank()) {
                                return cb.conjunction();
                        }

                        String pattern = "%" + keyword.toLowerCase() + "%";

                        return cb.or(cb.like(cb.lower(root.get("requestNumber")),pattern));
                };
        }

        public static Specification<InventoryRequest> hasRequestId(Long requestId) {

                return (root, query, cb) -> cb.equal(root.get("requestId"), requestId);
        }

        public static Specification<InventoryRequest> hasRequestNumber(String requestNumber) {

                return (root, query, cb) -> {

                        if(requestNumber == null || requestNumber.isBlank()) {
                                return cb.conjunction();
                        }

                        return cb.like(
                                cb.lower(root.get("requestNumber")), "%" + requestNumber.toLowerCase() + "%"
                        );
                };
        }

        public static Specification<InventoryRequest> hasEmployeeName(String employeeName) {

                return (root, query, cb) -> {

                        if(employeeName == null || employeeName.isBlank()) {
                                return cb.conjunction();
                        }

                        return cb.like(
                                cb.lower(root.get("employee").get("employeeName")), "%" + employeeName.toLowerCase() + "%"
                        );
                };
        }

        public static Specification<InventoryRequest> hasStatus(RequestStatus status) {

                return (root, query, cb) -> {

                        if(status == null) {
                                return cb.conjunction();
                        }

                        return cb.equal(root.get("requestStatus"),status);
                };
        }

        public static Specification<InventoryRequest> hasFromDate(LocalDate fromDate) {

                return (root, query, cb) -> {

                        if(fromDate == null) {
                                return cb.conjunction();
                        }

                        return cb.greaterThanOrEqualTo(root.get("requestDate"), fromDate.atStartOfDay());
                };
        }

        public static Specification<InventoryRequest> hasToDate(LocalDate toDate) {

                return (root, query, cb) -> {

                        if(toDate == null) {
                                return cb.conjunction();
                        }

                        return cb.lessThanOrEqualTo(
                                root.get("requestDate"), toDate.atTime(23, 59, 59)
                        );
                };
        }

        public static Specification<InventoryRequest> hasEmployeeId(Long employeeId) {

                return (root, query, cb) -> {

                        if(employeeId == null) {
                                return cb.conjunction();
                        }

                        return cb.equal(root.get("employee").get("employeeId"), employeeId);
                };
        }

        
}