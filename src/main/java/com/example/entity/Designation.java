package com.example.entity;

import com.example.entity.base.BaseEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name="designations")
public class Designation extends BaseEntity{
    
    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    @Column(name= "designation_id")
    private Long designationId;

    @NotBlank
    @Column(name="designation_name" , nullable=false, unique=true)
    private String designationName;

    @Column(name="designation_code", unique=true)
    private String designationCode;

    @Column(name="is_active")
    private Boolean isActive;

    public Designation() {
    }

    public Designation(Long designationId, @NotBlank String designationName, String designationCode, Boolean isActive) {
        this.designationId = designationId;
        this.designationName = designationName;
        this.designationCode = designationCode;
        this.isActive = isActive;
    }

    public Long getDesignationId() {
        return designationId;
    }

    public void setDesignationId(Long designationId) {
        this.designationId = designationId;
    }

    public String getDesignationName() {
        return designationName;
    }

    public void setDesignationName(String designationName) {
        this.designationName = designationName;
    }

    public String getDesignationCode() {
        return designationCode;
    }

    public void setDesignationCode(String designationCode) {
        this.designationCode = designationCode;
    }

    public Boolean getIsActive() {
        return isActive;
    }

    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
    }

    
    
}
