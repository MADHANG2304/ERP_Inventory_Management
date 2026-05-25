package com.example.specification;

import org.springframework.data.jpa.domain.Specification;

import com.example.entity.InventoryCategory;

public class InventoryCategorySpecification {
    
    public static Specification<InventoryCategory> searchCategory(String keyword){

        return (root, query, cb) -> {
            
            if(keyword == null || keyword.isBlank()){
                return cb.conjunction();      
            }

            String pattern = "%" + keyword.toLowerCase() + "%";

            return cb.or(
                cb.like(
                    cb.lower(
                        root.get("categoryName")
                    ), pattern
                )
            );
        };
    }
}
