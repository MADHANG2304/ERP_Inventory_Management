package com.example.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import com.example.dto.DesignationDTO;
import com.example.entity.Designation;
import com.example.repository.DesignationRepository;
import com.example.specification.DesignationSpecification;

@Service
public class DesignationService {
    
    private final DesignationRepository designationRepository;

    public DesignationService(DesignationRepository designationRepository){
        this.designationRepository = designationRepository;
    }


    public DesignationDTO saveDesignation(DesignationDTO dto){
        validateDesignation(dto);

        Designation designation;

        if(dto.getDesignationId() != null){
            designation = designationRepository
                .findById(dto.getDesignationId())
                .orElse(new Designation());
        }
        else{
            designation = new Designation();
        }

        designation.setDesignationName(dto.getDesignationName().trim());
        designation.setDesignationCode(dto.getDesignationCode());
        designation.setIsActive(dto.getIsActive());

        Designation saved = designationRepository.save(designation);
        
        return convertToDTO(saved);
    }

    public List<DesignationDTO> getAllDesignation(){
        return designationRepository
            .findAll()
            .stream()
            .map(this::convertToDTO)
            .collect(Collectors.toList());
    }

    public List<DesignationDTO> searchDesignation(String keyword){
        Specification<Designation> spec = DesignationSpecification
            .searchDesignation(keyword);

        return designationRepository
            .findAll(spec)
            .stream()
            .map(this::convertToDTO)
            .collect(Collectors.toList());
    }

    public void deleteDesignation(Long id){
        designationRepository.deleteById(id);
    }


    public void validateDesignation(DesignationDTO dto){

        if(dto.getDesignationName() == null || dto.getDesignationName().isBlank()){
            throw new RuntimeException("Designation name is required");
        }

        if(dto.getDesignationCode() == null || dto.getDesignationCode().isBlank()){
            throw new RuntimeException("Designation code is required");
        }

        boolean duplicateName = designationRepository.findAll()
            .stream()
            .anyMatch(d -> 
                d.getDesignationName().equalsIgnoreCase(dto.getDesignationName()) &&
                !d.getDesignationId().equals(dto.getDesignationId())
            );

        if(duplicateName){
            throw new RuntimeException("Designation Name already exists");
        }

        boolean duplicateCode = designationRepository.findAll()
            .stream()
            .anyMatch(d -> 
                d.getDesignationCode().equalsIgnoreCase(dto.getDesignationCode()) && 
                !d.getDesignationId().equals(dto.getDesignationId())
            );
        
        if(duplicateCode){
            throw new RuntimeException("Designation code already exists");
        }
    }

    private DesignationDTO convertToDTO(Designation designation){

        DesignationDTO dto = new DesignationDTO();

        dto.setDesignationId(designation.getDesignationId());
        dto.setDesignationName(designation.getDesignationName());
        dto.setDesignationCode(designation.getDesignationCode());
        dto.setIsActive(designation.getIsActive());

        return dto;
    }
    
}
