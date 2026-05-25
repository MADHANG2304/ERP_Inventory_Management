package com.example.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.entity.ApprovalConfigLevel;

@Repository
public interface ApprovalConfigLevelRepository
        extends JpaRepository<ApprovalConfigLevel, Long> {
}