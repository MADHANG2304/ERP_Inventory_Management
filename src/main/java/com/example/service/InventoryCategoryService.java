package com.example.service;

import java.util.List;
import java.util.stream.Collectors;

import com.example.repository.InventoryCategoryRepository;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import com.example.dto.InventoryCategoryDTO;
import com.example.entity.InventoryCategory;
import com.example.specification.InventoryCategorySpecification;

@Service
public class InventoryCategoryService {
    
    private final InventoryCategoryRepository repository;

    public InventoryCategoryService(InventoryCategoryRepository repository){
        this.repository = repository;
    }

    public InventoryCategoryDTO saveCategory(InventoryCategoryDTO dto){
        validateCategory(dto);
        
        InventoryCategory category;
        
        if(dto.getCategoryId() != null){
            category = repository
            .findById(dto.getCategoryId())
            .orElse(new InventoryCategory());
        }
        else{
            category = new InventoryCategory();
        }
        
        category.setCategoryName(dto.getCategoryName().trim());
        category.setDescription(dto.getDescription());
        category.setIsActive(dto.getIsActive());
        
        InventoryCategory saved = repository.save(category);

        return convertToDTO(saved);
    }

    public List<InventoryCategoryDTO> getAllCategory(){
        return repository
            .findAll()
            .stream()
            .map(this::convertToDTO)
            .collect(Collectors.toList());
    }

    public List<InventoryCategoryDTO> searchCategory(String keyword){
        Specification<InventoryCategory> spec = InventoryCategorySpecification
            .searchCategory(keyword);

        return repository
            .findAll(spec)
            .stream()
            .map(this::convertToDTO)
            .collect(Collectors.toList());
    }

    public void deleteCategory(Long id){
        repository.deleteById(id);
    }

    private void validateCategory(InventoryCategoryDTO dto){

        if(dto.getCategoryName() == null || dto.getCategoryName().isBlank()){
            throw new RuntimeException("Category Name is Required");
        }

        boolean duplicateCategory = repository.findAll()
            .stream()
            .anyMatch(c -> 
                c.getCategoryName().equalsIgnoreCase(dto.getCategoryName()) &&
                !c.getCategoryId().equals(dto.getCategoryId())
            );
        
        if(duplicateCategory){
            throw new RuntimeException("Category already exists");
        }
    }

    private InventoryCategoryDTO convertToDTO(InventoryCategory category){
        InventoryCategoryDTO dto = new InventoryCategoryDTO();

        dto.setCategoryId(category.getCategoryId());
        dto.setCategoryName(category.getCategoryName());
        dto.setDescription(category.getDescription());
        dto.setIsActive(category.getIsActive());

        return dto;
    }
}
