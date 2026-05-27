package com.example.specification;

import com.example.entity.AuditLog;
import org.springframework.data.jpa.domain.Specification;

public class AuditLogSpecification {

    public static Specification<AuditLog>
    searchLogs(String keyword) {

        return (root, query, cb) -> {

            if(keyword == null
                    || keyword.isBlank()) {

                return cb.conjunction();
            }

            String pattern =
                    "%" + keyword.toLowerCase() + "%";

            return cb.or(

                    cb.like(
                            cb.lower(
                                    root.get("username")
                            ),
                            pattern
                    ),

                    cb.like(
                            cb.lower(
                                    root.get("moduleName")
                            ),
                            pattern
                    ),

                    cb.like(
                            cb.lower(
                                    root.get("actionType")
                            ),
                            pattern
                    ),

                    cb.like(
                            cb.lower(
                                    root.get("description")
                            ),
                            pattern
                    )
            );
        };
    }
}