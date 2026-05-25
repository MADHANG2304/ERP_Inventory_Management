package com.example.specification;

import org.springframework.data.jpa.domain.Specification;

import com.example.entity.InventoryStock;

public class InventoryStockSpecification {

    public static Specification<InventoryStock> searchStock(String keyword) {

        return (root, query, criteriaBuilder) -> {

            if(keyword == null || keyword.isBlank()) {
                return criteriaBuilder.conjunction();
            }

            String pattern = "%" + keyword.toLowerCase() + "%";

            return criteriaBuilder.or(
                    criteriaBuilder.like(
                            criteriaBuilder.lower(root.get("item")
                                            .get("itemName")),pattern
                    ),
                    criteriaBuilder.like(
                            criteriaBuilder.lower(root.get("item")
                                            .get("itemCode")),pattern
                    )
            );
        };
    }

    public static Specification<InventoryStock>
    hasItemName(String itemName) {

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

    public static Specification<InventoryStock>
    hasItemCode(String itemCode) {

        return (root, query, cb) -> {

            if(itemCode == null
                    || itemCode.isBlank()) {

                return cb.conjunction();
            }

            return cb.like(

                    cb.lower(
                            root.get("item")
                                    .get("itemCode")
                    ),

                    "%" +
                            itemCode.toLowerCase()
                            + "%"
            );
        };
    }

    public static Specification<InventoryStock>
    hasStockStatus(Boolean lowStock) {

        return (root, query, cb) -> {

            if(lowStock == null) {

                return cb.conjunction();
            }

            if(lowStock) {

                return cb.lessThanOrEqualTo(

                        root.get("availableQuantity"),

                        root.get("item")
                                .get("minimumStock")
                );
            }

            return cb.greaterThan(

                    root.get("availableQuantity"),

                    root.get("item")
                            .get("minimumStock")
            );
        };
    }

    
}
