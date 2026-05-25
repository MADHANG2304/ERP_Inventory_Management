package com.example.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import com.example.entity.Roles;

public interface RoleRepository extends JpaRepository<Roles, Long>, JpaSpecificationExecutor<Roles>{
    
}
