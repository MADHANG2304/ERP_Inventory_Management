package com.example.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import com.example.dto.InventoryCategoryDTO;
import com.example.dto.InventoryItemDTO;
import com.example.dto.InventoryItemFilterDTO;
import com.example.entity.InventoryCategory;
import com.example.entity.InventoryItem;
import com.example.repository.InventoryCategoryRepository;
import com.example.repository.InventoryItemRepository;
import com.example.specification.InventoryItemSpecification;

@Service
public class InventoryItemService {
    
    private final InventoryItemRepository itemRepository;
    private final InventoryCategoryRepository categoryRepository;

    public InventoryItemService(InventoryItemRepository itemRepository, InventoryCategoryRepository categoryRepository){
        this.itemRepository = itemRepository;
        this.categoryRepository = categoryRepository;
    }

    public InventoryItemDTO saveItem(InventoryItemDTO dto){

        validateItem(dto);

        InventoryItem item;

        if(dto.getItemId() != null){
            item = itemRepository
                .findById(dto.getItemId())
                .orElse(new InventoryItem());
        }
        else{
            item = new InventoryItem();
        }

        InventoryCategory category = categoryRepository
            .findById(dto.getCategoryId())
            .orElseThrow(() -> new RuntimeException("Category not found"));

        item.setCategory(category);
        item.setItemName(dto.getItemName().trim());
        item.setItemCode(dto.getItemCode().trim().toUpperCase());
        item.setDescription(dto.getDescription());
        item.setIsReusable(dto.getIsReusable());

        if(Boolean.FALSE.equals(dto.getIsReusable())) {
            item.setAllowReturn(false);
        } 
        else {
            item.setAllowReturn(true);
        }

        item.setApprovalType(dto.getApprovalType());
        item.setMinimumStock(dto.getMinimumStock());
        item.setUnitType(dto.getUnitType());
        item.setStatus(dto.getStatus());

        InventoryItem saved = itemRepository.save(item);

        return convertToDTO(saved);
    }

    public List<InventoryItemDTO> getAllItems(){
        return itemRepository
            .findAll()
            .stream()
            .map(this::convertToDTO)
            .collect(Collectors.toList());
    }

     public List<InventoryCategoryDTO> getActiveCategories() {

        return categoryRepository
                .findAll()
                .stream()
                .filter(category ->
                    Boolean.TRUE.equals(category.getIsActive())
                )
                .map(category -> {

                    InventoryCategoryDTO dto = new InventoryCategoryDTO();

                    dto.setCategoryId(category.getCategoryId());
                    dto.setCategoryName(category.getCategoryName());

                    return dto;
                })
                .collect(Collectors.toList());
    }

    public List<InventoryItemDTO> searchItems(String keyword){
        Specification<InventoryItem> spec = InventoryItemSpecification
            .searchItems(keyword);

        return itemRepository
            .findAll(spec)
            .stream()
            .map(this::convertToDTO) 
            .collect(Collectors.toList());    
    }

    public void deleteItem(Long id){
        itemRepository.deleteById(id);
    }


    public List<InventoryItemDTO> filterItems(InventoryItemFilterDTO filterDTO) {

        Specification<InventoryItem> specification =

                InventoryItemSpecification
                        .hasCategory(filterDTO.getCategoryId())

                        .and(InventoryItemSpecification.hasItemCode(filterDTO.getItemCode()))

                        .and(InventoryItemSpecification.hasApprovalType(filterDTO.getApprovalType()))

                        .and(InventoryItemSpecification.hasStatus(filterDTO.getStatus()));

        return itemRepository
                .findAll(specification)
                .stream()
                .map(this::convertToDTO)
                .toList();
    }
    

    private void validateItem(InventoryItemDTO dto){

        if(dto.getCategoryId() == null){
            throw new RuntimeException("Category is required");
        }
        if(dto.getItemName() == null){
            throw new RuntimeException("Item Name is required");
        }
        if(dto.getItemCode() == null){
            throw new RuntimeException("Item Code is required");
        }
        if(dto.getApprovalType() == null){
            throw new RuntimeException("Approval Type is required");
        }
        if(dto.getUnitType() == null){
            throw new RuntimeException("Unit Type is required");
        }
        if(dto.getMinimumStock() == null || dto.getMinimumStock() < 0){
            throw new RuntimeException("Minimum stock must be valid");
        }

        boolean duplicateItem = itemRepository
            .findAll()
            .stream()
            .anyMatch(i -> 
                i.getItemCode().equalsIgnoreCase(dto.getItemCode()) &&
                !i.getItemId().equals(dto.getItemId())
        );

        if(duplicateItem){
            throw new RuntimeException("Item Code already exists");
        }
    }

    private InventoryItemDTO convertToDTO(InventoryItem item){
        InventoryItemDTO dto = new InventoryItemDTO();

        dto.setItemId(item.getItemId());
        dto.setCategoryId(item.getCategory().getCategoryId());
        dto.setCategoryName(item.getCategory().getCategoryName());
        dto.setItemName(item.getItemName());
        dto.setItemCode(item.getItemCode());
        dto.setDescription(item.getDescription());
        dto.setIsReusable(item.getIsReusable());
        dto.setAllowReturn(item.getAllowReturn());
        dto.setApprovalType(item.getApprovalType());
        dto.setMinimumStock(item.getMinimumStock());
        dto.setUnitType(item.getUnitType());
        dto.setStatus(item.getStatus());

        return dto;
    }

}
