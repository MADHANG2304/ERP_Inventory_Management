package com.example.service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import com.example.dto.ApprovalConfigDTO;
import com.example.dto.ApprovalConfigLevelDTO;
import com.example.entity.ApprovalConfig;
import com.example.entity.ApprovalConfigLevel;
import com.example.repository.ApprovalConfigRepository;
import com.example.specification.ApprovalConfigSpecification;

@Service
public class ApprovalConfigService {

    private final ApprovalConfigRepository
            approvalConfigRepository;

    public ApprovalConfigService(
            ApprovalConfigRepository approvalConfigRepository
    ) {

        this.approvalConfigRepository =
                approvalConfigRepository;
    }

    public ApprovalConfigDTO saveConfig(
            ApprovalConfigDTO dto
    ) {

        validateConfig(dto);

        ApprovalConfig config;

        if(dto.getConfigId() != null) {

            config =
                    approvalConfigRepository
                            .findById(dto.getConfigId())
                            .orElse(new ApprovalConfig());

        } else {

            config = new ApprovalConfig();
        }

        config.setConfigName(
                dto.getConfigName()
        );

        config.setRequestType(
                dto.getRequestType()
        );

        config.setIsActive(
                dto.getIsActive()
        );

        config.getLevels().clear();

        for(ApprovalConfigLevelDTO levelDTO
                : dto.getLevels()) {

            ApprovalConfigLevel level =
                    new ApprovalConfigLevel();

            level.setApprovalConfig(config);

            level.setApprovalOrder(
                    levelDTO.getApprovalOrder()
            );

            level.setApprovalRole(
                    levelDTO.getApprovalRole()
            );

            config.getLevels().add(level);
        }

        ApprovalConfig savedConfig =
                approvalConfigRepository.save(config);

        return convertToDTO(savedConfig);
    }

    public List<ApprovalConfigDTO> getAllConfigs() {

        return approvalConfigRepository
                .findAll()
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public List<ApprovalConfigDTO> searchConfigs(
            String keyword
    ) {

        Specification<ApprovalConfig> specification =
                ApprovalConfigSpecification
                        .searchConfig(keyword);

        return approvalConfigRepository
                .findAll(specification)
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public void deleteConfig(
            Long configId
    ) {

        approvalConfigRepository.deleteById(
                configId
        );
    }

    private void validateConfig(
            ApprovalConfigDTO dto
    ) {

        if(dto.getConfigName() == null
                || dto.getConfigName().isBlank()) {

            throw new RuntimeException(
                    "Config name required"
            );
        }

        if(dto.getRequestType() == null) {

            throw new RuntimeException(
                    "Request type required"
            );
        }

        if(dto.getLevels() == null
                || dto.getLevels().isEmpty()) {

            throw new RuntimeException(
                    "At least one approval level required"
            );
        }

        Set<Integer> orders =
                new HashSet<>();

        Set<String> roles =
                new HashSet<>();

        for(ApprovalConfigLevelDTO level
                : dto.getLevels()) {

            if(level.getApprovalOrder() == null
                    || level.getApprovalOrder() <= 0) {

                throw new RuntimeException(
                        "Invalid approval order"
                );
            }

            if(!orders.add(
                    level.getApprovalOrder()
            )) {

                throw new RuntimeException(
                        "Duplicate approval order"
                );
            }

            if(!roles.add(
                    level.getApprovalRole().name()
            )) {

                throw new RuntimeException(
                        "Duplicate approval role"
                );
            }
        }
    }

    private ApprovalConfigDTO convertToDTO(
            ApprovalConfig config
    ) {

        ApprovalConfigDTO dto =
                new ApprovalConfigDTO();

        dto.setConfigId(
                config.getConfigId()
        );

        dto.setConfigName(
                config.getConfigName()
        );

        dto.setRequestType(
                config.getRequestType()
        );

        dto.setIsActive(
                config.getIsActive()
        );

        dto.setLevels(

                config.getLevels()
                        .stream()
                        .map(level -> {

                            ApprovalConfigLevelDTO
                                    levelDTO =
                                    new ApprovalConfigLevelDTO();

                            levelDTO.setLevelId(
                                    level.getLevelId()
                            );

                            levelDTO.setApprovalOrder(
                                    level.getApprovalOrder()
                            );

                            levelDTO.setApprovalRole(
                                    level.getApprovalRole()
                            );

                            return levelDTO;
                        })
                        .collect(Collectors.toList())
        );

        return dto;
    }
}