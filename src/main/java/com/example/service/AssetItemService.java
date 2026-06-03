package com.example.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.example.dto.AssetItemDTO;
import com.example.dto.InventoryItemDTO;
import com.example.entity.AssetItem;
import com.example.entity.InventoryItem;
import com.example.enums.AssetStatus;
import com.example.repository.AssetItemRepository;
import com.example.repository.InventoryItemRepository;

@Service
public class AssetItemService {

    private final AssetItemRepository assetItemRepository;

    private final InventoryItemRepository inventoryItemRepository;

    private final AuditLogService auditLogService;

    public AssetItemService(
            AssetItemRepository assetItemRepository,
            InventoryItemRepository inventoryItemRepository,
            AuditLogService auditLogService
    ) {

        this.assetItemRepository =
                assetItemRepository;

        this.inventoryItemRepository =
                inventoryItemRepository;

        this.auditLogService =
                auditLogService;
    }

    public AssetItemDTO saveAsset(
            AssetItemDTO dto
    ) {

        validateAsset(dto);

        AssetItem asset;

        if(dto.getAssetItemId() != null) {

            asset = assetItemRepository
                    .findById(dto.getAssetItemId())
                    .orElse(new AssetItem());

        } else {

            asset = new AssetItem();
        }

        InventoryItem item =
                inventoryItemRepository
                        .findById(dto.getItemId())
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Item not found"
                                )
                        );

        asset.setItem(item);

        asset.setAssetReferenceNumber(
                dto.getAssetReferenceNumber()
        );

        asset.setModelNumber(
                dto.getModelNumber()
        );

        asset.setPurchaseDate(
                dto.getPurchaseDate()
        );

        asset.setPurchasePrice(
                dto.getPurchasePrice()
        );

        asset.setAssetStatus(
                dto.getAssetStatus()
        );

        AssetItem saved =
                assetItemRepository.save(asset);

        auditLogService.logAction(

                "ASSET_MODULE",

                dto.getAssetItemId() == null
                        ? "CREATE"
                        : "UPDATE",

                "Asset saved : "
                        + dto.getAssetReferenceNumber()
        );

        return convertToDTO(saved);
    }

    public List<AssetItemDTO> getAllAssets() {

        return assetItemRepository
                .findAll()
                .stream()
                .map(this::convertToDTO)
                .toList();
    }

    private void validateAsset(
        AssetItemDTO dto
    ) {

        if(dto.getItemId() == null) {

            throw new RuntimeException(
                    "Item required"
            );
        }

        if(dto.getAssetReferenceNumber() == null
                ||
                dto.getAssetReferenceNumber().isBlank()) {

            throw new RuntimeException(
                    "Asset Reference required"
            );
        }

        boolean duplicateReference =
                assetItemRepository
                        .findAll()
                        .stream()

                        .anyMatch(asset ->

                                asset.getAssetReferenceNumber()
                                        .equalsIgnoreCase(
                                                dto.getAssetReferenceNumber()
                                        )

                                &&

                                (
                                    dto.getAssetItemId() == null
                                    ||
                                    !asset.getAssetItemId()
                                            .equals(
                                                    dto.getAssetItemId()
                                            )
                                )
                        );

        if(duplicateReference) {

            throw new RuntimeException(
                    "Asset Reference already exists"
            );
        }

        if(dto.getAssetStatus() == null) {

            dto.setAssetStatus(
                    AssetStatus.AVAILABLE
            );
        }
    }

    public List<InventoryItemDTO>
    getReusableItems() {

        return inventoryItemRepository
                .findByIsReusableTrue()
                .stream()

                .map(item -> {

                    InventoryItemDTO dto =
                            new InventoryItemDTO();

                    dto.setItemId(
                            item.getItemId()
                    );

                    dto.setItemName(
                            item.getItemName()
                    );

                    dto.setItemCode(
                            item.getItemCode()
                    );

                    return dto;
                })

                .toList();
    }

        public List<AssetItemDTO> getAvailableAssetsByItem(
                Long itemId
        ) {

        return assetItemRepository
                .findAll()
                .stream()

                .filter(asset ->

                        asset.getItem()
                                .getItemId()
                                .equals(itemId)

                        &&

                        asset.getAssetStatus()
                                == AssetStatus.AVAILABLE
                )

                .map(this::convertToDTO)

                .toList();
        }

    private AssetItemDTO convertToDTO(
            AssetItem asset
    ) {

        AssetItemDTO dto =
                new AssetItemDTO();

        dto.setAssetItemId(
                asset.getAssetItemId()
        );

        dto.setItemId(
                asset.getItem().getItemId()
        );

        dto.setItemName(
                asset.getItem().getItemName()
        );

        dto.setItemCode(
                asset.getItem().getItemCode()
        );

        dto.setAssetReferenceNumber(
                asset.getAssetReferenceNumber()
        );

        dto.setModelNumber(
                asset.getModelNumber()
        );

        dto.setPurchaseDate(
                asset.getPurchaseDate()
        );

        dto.setPurchasePrice(
                asset.getPurchasePrice()
        );

        dto.setAssetStatus(
                asset.getAssetStatus()
        );

        return dto;
    }
}