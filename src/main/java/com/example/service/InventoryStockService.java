package com.example.service;

import com.example.dto.InventoryItemDTO;
import com.example.dto.InventoryStockDTO;
import com.example.dto.InventoryStockFilterDTO;
import com.example.entity.InventoryItem;
import com.example.entity.InventoryStock;
import com.example.repository.InventoryItemRepository;
import com.example.repository.InventoryStockRepository;
import com.example.specification.InventoryStockSpecification;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class InventoryStockService {

    private final InventoryStockRepository inventoryStockRepository;

    private final InventoryItemRepository inventoryItemRepository;

    private final AuditLogService auditLogService;

    public InventoryStockService(InventoryStockRepository inventoryStockRepository, InventoryItemRepository inventoryItemRepository, AuditLogService auditLogService) {
        this.inventoryStockRepository = inventoryStockRepository;
        this.inventoryItemRepository = inventoryItemRepository;
        this.auditLogService = auditLogService;
    }

    public InventoryStockDTO saveStock(InventoryStockDTO dto) {

        validateStock(dto);

        InventoryStock stock;

        if(dto.getStockId() != null) {
            stock = inventoryStockRepository
                    .findById(dto.getStockId())
                    .orElse(new InventoryStock());

        } else {
            stock = new InventoryStock();
        }

        InventoryItem item = inventoryItemRepository
                    .findById(dto.getItemId())
                    .orElseThrow(() ->
                            new RuntimeException(
                                    "Item not found"
                            )
                    );

        stock.setItem(item);

        stock.setAvailableQuantity(dto.getAvailableQuantity());

        stock.setIssuedQuantity(dto.getIssuedQuantity());

        stock.setDamagedQuantity(dto.getDamagedQuantity());

        InventoryStock savedStock = inventoryStockRepository.save(stock);

        auditLogService.logAction(

                "STOCK_MODULE",

                "STOCK_UPDATE",

                "Stock updated for item : "
                        + item.getItemName()
        );

        return convertToDTO(savedStock);
    }

    public List<InventoryStockDTO> getAllStocks() {
        return inventoryStockRepository
                .findAll()
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public List<InventoryStockDTO> searchStocks(String keyword) {

        Specification<InventoryStock> specification = InventoryStockSpecification.searchStock(keyword);

        return inventoryStockRepository
                .findAll(specification)
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public List<InventoryStockDTO>
    filterStocks(
            InventoryStockFilterDTO filterDTO
    ) {

        Specification<InventoryStock> specification =
                InventoryStockSpecification
                        .hasItemName(filterDTO.getItemName())

                        .and(InventoryStockSpecification.hasItemCode(filterDTO.getItemCode()))

                        .and(InventoryStockSpecification.hasStockStatus(filterDTO.getLowStock()));

        return inventoryStockRepository
                .findAll(specification)
                .stream()
                .map(this::convertToDTO)
                .toList();
    }

    public List<InventoryItemDTO> getAllItems() {

        return inventoryItemRepository
                .findAll()
                .stream()
                .map(item -> {

                    InventoryItemDTO dto = new InventoryItemDTO();

                    dto.setItemId(item.getItemId());

                    dto.setItemName(item.getItemName());

                    dto.setItemCode(item.getItemCode());

                    dto.setMinimumStock(item.getMinimumStock());

                    return dto;
                })
                .collect(Collectors.toList());
    }

    private void validateStock(InventoryStockDTO dto) {

        if(dto.getItemId() == null) {
            throw new RuntimeException("Item is required");
        }

        if(dto.getAvailableQuantity() == null || dto.getAvailableQuantity() < 0) {
            throw new RuntimeException("Available quantity invalid");
        }

        if(dto.getIssuedQuantity() == null || dto.getIssuedQuantity() < 0) {
            throw new RuntimeException("Issued quantity invalid");
        }

        if(dto.getDamagedQuantity() == null || dto.getDamagedQuantity() < 0) {
            throw new RuntimeException("Damaged quantity invalid");
        }
    }

    private InventoryStockDTO convertToDTO(InventoryStock stock) {

        InventoryStockDTO dto = new InventoryStockDTO();

        dto.setStockId(stock.getStockId());

        dto.setItemId(stock.getItem().getItemId());

        dto.setItemName(stock.getItem().getItemName());

        dto.setItemCode(stock.getItem().getItemCode());

        dto.setAvailableQuantity(stock.getAvailableQuantity());

        dto.setIssuedQuantity(stock.getIssuedQuantity());

        dto.setDamagedQuantity(stock.getDamagedQuantity());

        dto.setLowStock(stock.getAvailableQuantity() <= stock.getItem().getMinimumStock());

        return dto;
    }
}