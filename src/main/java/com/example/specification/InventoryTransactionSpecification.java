package com.example.specification;

import com.example.entity.InventoryTransaction;
import com.example.enums.ReferenceType;
import com.example.enums.TransactionType;

import org.springframework.data.jpa.domain.Specification;

public class InventoryTransactionSpecification {

    public static Specification<InventoryTransaction>
    searchTransaction(
            String keyword
    ) {

        return (root, query, criteriaBuilder) -> {

            if(keyword == null
                    || keyword.isBlank()) {

                return criteriaBuilder.conjunction();
            }

            String pattern =
                    "%" + keyword.toLowerCase() + "%";

            return criteriaBuilder.or(

                    criteriaBuilder.like(
                            criteriaBuilder.lower(
                                    root.get("item")
                                            .get("itemName")
                            ),
                            pattern
                    ),

                    criteriaBuilder.like(
                            criteriaBuilder.lower(
                                    root.get("transactionType")
                                            .as(String.class)
                            ),
                            pattern
                    ),

                    criteriaBuilder.like(
                            criteriaBuilder.lower(
                                    root.get("referenceType")
                                            .as(String.class)
                            ),
                            pattern
                    ),

                    criteriaBuilder.like(
                            criteriaBuilder.lower(
                                    root.get("referenceNumber")
                            ),
                            pattern
                    )
            );
        };
    }

    public static Specification<InventoryTransaction>
                hasItemName(
                        String itemName
                ) {

                return (root, query, cb) -> {

                        if(itemName == null
                                || itemName.isBlank()) {

                        return cb.conjunction();
                        }

                        return cb.like(

                                cb.lower(
                                        root.get("item")
                                                .get("itemName")
                                ),

                                "%" +
                                        itemName.toLowerCase()
                                        + "%"
                        );
                };
        }

        public static Specification<InventoryTransaction>
                hasTransactionType(
                        TransactionType transactionType
                ) {

                return (root, query, cb) -> {

                        if(transactionType == null) {

                        return cb.conjunction();
                        }

                        return cb.equal(

                                root.get("transactionType"),
                                transactionType
                        );
                };
        }

        public static Specification<InventoryTransaction>
                hasReferenceType(
                        ReferenceType referenceType
                ) {

                return (root, query, cb) -> {

                        if(referenceType == null) {

                        return cb.conjunction();
                        }

                        return cb.equal(

                                root.get("referenceType"),
                                referenceType
                        );
                };
        }

        public static Specification<InventoryTransaction>
                hasReferenceNumber(
                        String referenceNumber
                ) {

                return (root, query, cb) -> {

                        if(referenceNumber == null
                                || referenceNumber.isBlank()) {

                        return cb.conjunction();
                        }

                        return cb.like(

                                cb.lower(
                                        root.get("referenceNumber")
                                ),

                                "%" +
                                        referenceNumber.toLowerCase()
                                        + "%"
                        );
                };
        }
}