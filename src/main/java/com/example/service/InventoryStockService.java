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

import com.example.enums.AssetStatus;
import com.example.repository.AssetItemRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class InventoryStockService {

    private final InventoryStockRepository inventoryStockRepository;

    private final InventoryItemRepository inventoryItemRepository;

    private final AuditLogService auditLogService;

    private final AssetItemRepository assetItemRepository;

    public InventoryStockService(InventoryStockRepository inventoryStockRepository, InventoryItemRepository inventoryItemRepository, AuditLogService auditLogService, AssetItemRepository assetItemRepository) {
        this.inventoryStockRepository = inventoryStockRepository;
        this.inventoryItemRepository = inventoryItemRepository;
        this.assetItemRepository = assetItemRepository;
        this.auditLogService = auditLogService;
    }

        public InventoryStockDTO saveStock(InventoryStockDTO dto){

                validateStock(dto);

                InventoryStock stock;

                if(dto.getStockId() != null) {
                        stock = inventoryStockRepository.findById(dto.getStockId())
                                .orElse(new InventoryStock());

                } else {
                        stock = new InventoryStock();
                }

                InventoryItem item = inventoryItemRepository
                                .findById(dto.getItemId())
                                .orElseThrow(() ->
                                        new RuntimeException("Item not found")
                                );

                if(Boolean.TRUE.equals(item.getIsReusable())){
                        throw new RuntimeException("Cannot manually update stock for reusable items");
                }

                stock.setItem(item);

                stock.setAvailableQuantity(dto.getAvailableQuantity());

                stock.setIssuedQuantity(dto.getIssuedQuantity());

                InventoryStock savedStock = inventoryStockRepository.save(stock);

                auditLogService.logAction(

                        "STOCK_MODULE",

                        "STOCK_UPDATE",

                        "Stock updated for item : " + item.getItemName()
                );

                return convertToDTO(savedStock);
        }

        public List<InventoryStockDTO> getAllStocks() {

                List<InventoryStockDTO> stocks = inventoryStockRepository
                                .findAll()
                                .stream()
                                .map(this::convertToDTO)
                                .collect(Collectors.toList());

                List<InventoryItem> reusableItems = inventoryItemRepository.findByIsReusableTrue();

                for(InventoryItem item : reusableItems) {

                        InventoryStockDTO dto = new InventoryStockDTO();

                        dto.setItemId(item.getItemId());

                        dto.setItemName(item.getItemName());

                        dto.setItemCode(item.getItemCode());

                        long available = assetItemRepository
                                .countByItemItemIdAndAssetStatus(item.getItemId(), AssetStatus.AVAILABLE);

                        long issued = assetItemRepository
                                .countByItemItemIdAndAssetStatus(item.getItemId(), AssetStatus.ISSUED);

                        long damaged = assetItemRepository
                                .countByItemItemIdAndAssetStatus(item.getItemId(), AssetStatus.DAMAGED);

                        dto.setAvailableQuantity((int) available);

                        dto.setIssuedQuantity((int) issued);

                        dto.setDamagedQuantity((int) damaged);

                        dto.setReusable(true);

                        dto.setLowStock(available <= item.getMinimumStock());

                        stocks.add(dto);
                }

                return stocks;
        }

        public List<InventoryStockDTO> searchStocks(String keyword) {

                Specification<InventoryStock> specification = InventoryStockSpecification.searchStock(keyword);

                return inventoryStockRepository
                        .findAll(specification)
                        .stream()
                        .map(this::convertToDTO)
                        .collect(Collectors.toList());
        }

        public List<InventoryStockDTO> filterStocks(InventoryStockFilterDTO filterDTO){

                Specification<InventoryStock> specification = InventoryStockSpecification
                                
                                .hasItemName(filterDTO.getItemName())

                                .and(InventoryStockSpecification.hasItemCode(filterDTO.getItemCode()))

                                .and(InventoryStockSpecification.hasStockStatus(filterDTO.getLowStock()));

                return inventoryStockRepository
                        .findAll(specification)
                        .stream()
                        .map(this::convertToDTO)
                        .toList();
        }

        private void validateStock(InventoryStockDTO dto){

                if(dto.getItemId() == null) {
                        throw new RuntimeException("Item is required");
                }

                InventoryItem item = inventoryItemRepository
                                .findById(dto.getItemId())
                                .orElseThrow(() ->
                                        new RuntimeException("Item not found")
                                );


                if(Boolean.TRUE.equals(item.getIsReusable())){
                        throw new RuntimeException("Reusable item stock is managed through Asset Items");
                }

                if(dto.getAvailableQuantity() == null || dto.getAvailableQuantity() < 0) {
                        throw new RuntimeException("Available quantity invalid");
                }

                if(dto.getIssuedQuantity() == null || dto.getIssuedQuantity() < 0) {
                        throw new RuntimeException("Issued quantity invalid");
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

        dto.setReusable(stock.getItem().getIsReusable());

        return dto;
    }
}