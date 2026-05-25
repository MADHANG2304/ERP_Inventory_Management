package com.example.dto;

public class InventoryCategoryDTO {
    
    private Long categoryId;

    private String categoryName;

    private String description;

    private Boolean isActive = true;

    public InventoryCategoryDTO() {
    }

    public InventoryCategoryDTO(Long categoryId, String categoryName, String description, Boolean isActive) {
        this.categoryId = categoryId;
        this.categoryName = categoryName;
        this.description = description;
        this.isActive = isActive;
    }

    public Long getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(Long categoryId) {
        this.categoryId = categoryId;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Boolean getIsActive() {
        return isActive;
    }

    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
    }  
    
    
}
