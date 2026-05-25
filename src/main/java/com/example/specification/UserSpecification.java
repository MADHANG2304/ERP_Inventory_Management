package com.example.specification;

import com.example.entity.User;
import org.springframework.data.jpa.domain.Specification;

public class UserSpecification {

    public static Specification<User> searchUsers(
            String keyword
    ) {

        return (root, query, criteriaBuilder) -> {

            if(keyword == null || keyword.isBlank()) {

                return criteriaBuilder.conjunction();
            }

            String pattern = "%" + keyword.toLowerCase() + "%";

            return criteriaBuilder.or(
                    criteriaBuilder.like(
                            criteriaBuilder.lower(
                                    root.get("username")
                            ),
                            pattern
                    ),

                    criteriaBuilder.like(
                            criteriaBuilder.lower(
                                    root.get("employee")
                                            .get("employeeName")
                            ),
                            pattern
                    ),

                    criteriaBuilder.like(
                            criteriaBuilder.lower(
                                    root.get("roles")
                                            .get("roleName")
                            ),
                            pattern
                    )
            );
        };

}
        public static Specification<User> hasUsername(String username) {
                return (root, query, criteriaBuilder) ->

                        criteriaBuilder.equal(
                                root.get("username"),
                                username
                );
        };
}