package com.example.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import com.example.dto.DepartmentDTO;
import com.example.entity.Department;
import com.example.repository.DepartmentRepository;
import com.example.specification.DepartmentSpecification;

@Service
public class DepartmentService {
    
    private final DepartmentRepository departmentRepository;

    public DepartmentService(DepartmentRepository departmentRepository){
        this.departmentRepository = departmentRepository;
    }

    public DepartmentDTO saveDepartment(DepartmentDTO dto){
        Department department;

        if(dto.getDepartmentId() != null){
            department = departmentRepository.findById(dto.getDepartmentId())
                .orElse(new Department());
        }
        else{
            department = new Department();
        }

        department.setDepartmentName(dto.getDepartmentName());
        department.setDepartmentCode(dto.getDepartmentCode());
        department.setIsActive(dto.getIsActive());

        Department saved = departmentRepository.save(department);
        
        DepartmentDTO response = new DepartmentDTO();
        
        response.setDepartmentId(saved.getDepartmentId());
        response.setDepartmentName(saved.getDepartmentName());
        response.setDepartmentCode(saved.getDepartmentCode());
        response.setIsActive(saved.getIsActive());

        return response;
    }

    public List<DepartmentDTO> getAllDepartments(){
        return departmentRepository
            .findAll()
            .stream()
            .map(this::convertToDTO)
            .collect(Collectors.toList());
    }

    public List<DepartmentDTO> searchDepartments(
            String keyword
    ) {

        Specification<Department> spec =
                DepartmentSpecification.searchDepartment(keyword);

        return departmentRepository
                .findAll(spec)
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    public void deleteDepartment(Long departmentId){
        departmentRepository.deleteById(departmentId);
    }

    private DepartmentDTO convertToDTO(Department department){
        DepartmentDTO dto = new DepartmentDTO();

        dto.setDepartmentId(department.getDepartmentId());
        dto.setDepartmentName(department.getDepartmentName());
        dto.setDepartmentCode(department.getDepartmentCode());
        dto.setIsActive(department.getIsActive());

        return dto;
    }
}
