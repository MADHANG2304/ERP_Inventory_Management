package com.example.service;

import com.example.dto.AuditLogDTO;
import com.example.entity.AuditLog;
import com.example.repository.AuditLogRepository;
import com.example.security.SecurityService;
import com.example.specification.AuditLogSpecification;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class AuditLogService {

    private final AuditLogRepository
            auditLogRepository;

    private final SecurityService
            securityService;

    public AuditLogService(
            AuditLogRepository auditLogRepository,
            SecurityService securityService
    ) {

        this.auditLogRepository =
                auditLogRepository;

        this.securityService =
                securityService;
    }

    public void logAction(

            String moduleName,

            String actionType,

            String description
    ) {

        AuditLog log =
                new AuditLog();

        log.setUsername(
                securityService
                        .getAuthenticatedUser()
        );

        log.setRoleName(
                securityService
                        .getAuthenticatedRole()
        );

        log.setModuleName(
                moduleName
        );

        log.setActionType(
                actionType
        );

        log.setDescription(
                description
        );

        log.setActionTime(
                LocalDateTime.now()
        );

        auditLogRepository.save(log);
    }

    public List<AuditLogDTO>
    getAllLogs() {

        return auditLogRepository
                .findAll()
                .stream()
                .map(this::convertToDTO)
                .toList();
    }

    public List<AuditLogDTO>
    searchLogs(
            String keyword
    ) {

        Specification<AuditLog>
                specification =

                AuditLogSpecification
                        .searchLogs(keyword);

        return auditLogRepository
                .findAll(specification)
                .stream()
                .map(this::convertToDTO)
                .toList();
    }

    private AuditLogDTO convertToDTO(
            AuditLog log
    ) {

        AuditLogDTO dto =
                new AuditLogDTO();

        dto.setAuditId(
                log.getAuditId()
        );

        dto.setUsername(
                log.getUsername()
        );

        dto.setRoleName(
                log.getRoleName()
        );

        dto.setModuleName(
                log.getModuleName()
        );

        dto.setActionType(
                log.getActionType()
        );

        dto.setDescription(
                log.getDescription()
        );

        dto.setActionTime(
                log.getActionTime()
        );

        return dto;
    }
}