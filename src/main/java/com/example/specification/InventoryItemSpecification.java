package com.example.specification;

import org.springframework.data.jpa.domain.Specification;

import com.example.entity.InventoryItem;
import com.example.enums.ApprovalType;
import com.example.enums.ItemStatus;

public class InventoryItemSpecification {
    
    public static Specification<InventoryItem> searchItems(String keyword){
        
        return (root, query, cb) -> {
            if(keyword == null || keyword.isBlank()){
                return cb.conjunction();
            }

            String pattern = "%" + keyword.toLowerCase() + "%";

            return cb.or(
                cb.like(
                    cb.lower(
                        root.get("itemName")), pattern
                ),
                cb.like(
                    cb.lower(
                        root.get("itemCode")), pattern
                ),
                cb.like(
                    cb.lower(
                        root.get("description")), pattern
                )
            );
        };
    }

    public static Specification<InventoryItem>
    hasCategory(
            Long categoryId
    ) {

        return (root, query, cb) -> {

            if(categoryId == null) {

                return cb.conjunction();
            }

            return cb.equal(

                    root.get("category")
                            .get("categoryId"),

                    categoryId
            );
        };
    }

    public static Specification<InventoryItem>
    hasItemCode(
            String itemCode
    ) {

        return (root, query, cb) -> {

            if(itemCode == null
                    || itemCode.isBlank()) {

                return cb.conjunction();
            }

            return cb.like(

                    cb.lower(
                            root.get("itemCode")
                    ),

                    "%" +
                            itemCode.toLowerCase()
                            + "%"
            );
        };
    }

    public static Specification<InventoryItem>
    hasApprovalType(
            ApprovalType approvalType
    ) {

        return (root, query, cb) -> {

            if(approvalType == null) {

                return cb.conjunction();
            }

            return cb.equal(

                    root.get("approvalType"),
                    approvalType
            );
        };
    }

    public static Specification<InventoryItem>
    hasStatus(
            ItemStatus status
    ) {

        return (root, query, cb) -> {

            if(status == null) {

                return cb.conjunction();
            }

            return cb.equal(

                    root.get("status"),
                    status
            );
        };
    }

    
}
