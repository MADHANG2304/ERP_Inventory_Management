package com.example.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import com.example.entity.Designation;

public interface DesignationRepository extends JpaRepository<Designation,Long>, JpaSpecificationExecutor<Designation>{
    
}
