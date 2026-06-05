package com.example.service;

import java.util.Comparator;
import java.util.List;

import org.springframework.stereotype.Service;

import com.example.dto.ProductTrackingDTO;
import com.example.entity.AssetItem;
import com.example.entity.IssuedItem;
import com.example.entity.ReturnedItem;
import com.example.repository.AssetItemRepository;
import com.example.repository.IssuedItemRepository;
import com.example.repository.ReturnedItemRepository;

@Service
public class ProductTrackingService {

    private final AssetItemRepository assetItemRepository;

    private final IssuedItemRepository issuedItemRepository;

    private final ReturnedItemRepository returnedItemRepository;

    public ProductTrackingService(
            AssetItemRepository assetItemRepository,
            IssuedItemRepository issuedItemRepository,
            ReturnedItemRepository returnedItemRepository
    ) {

        this.assetItemRepository =
                assetItemRepository;

        this.issuedItemRepository =
                issuedItemRepository;

        this.returnedItemRepository =
                returnedItemRepository;
    }

    public List<ProductTrackingDTO> getAllAssets() {

        return assetItemRepository
                .findAll()
                .stream()

                .sorted(
                        Comparator.comparing(
                                AssetItem::getAssetReferenceNumber,
                                String.CASE_INSENSITIVE_ORDER
                        )
                )

                .map(this::convertToDTO)

                .toList();
    }

    public ProductTrackingDTO getAssetDetails(
            Long assetItemId
    ) {

        AssetItem asset =
                assetItemRepository
                        .findById(assetItemId)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Asset not found"
                                )
                        );

        return convertToDTO(asset);
    }

    private ProductTrackingDTO convertToDTO(
            AssetItem asset
    ) {

        ProductTrackingDTO dto =
                new ProductTrackingDTO();

        dto.setAssetItemId(
                asset.getAssetItemId()
        );

        dto.setAssetReferenceNumber(
                asset.getAssetReferenceNumber()
        );

        dto.setItemName(
                asset.getItem().getItemName()
        );

        dto.setItemCode(
                asset.getItem().getItemCode()
        );

        dto.setModelNumber(
                asset.getModelNumber()
        );

        dto.setPurchaseDate(
                asset.getPurchaseDate()
        );

        dto.setPurchasePrice(
                asset.getPurchasePrice() == null
                        ? "-"
                        : asset.getPurchasePrice().toString()
        );

        dto.setAssetStatus(
                asset.getAssetStatus().name()
        );



        List<IssuedItem> issueHistory =
                issuedItemRepository
                        .findByAssetItemAssetItemId(
                                asset.getAssetItemId()
                        );

        if (!issueHistory.isEmpty()) {

            IssuedItem latestIssue =
                    issueHistory
                            .stream()
                            .max(
                                    Comparator.comparing(
                                            IssuedItem::getIssuedDate
                                    )
                            )
                            .orElse(null);

            if (latestIssue != null) {

                dto.setIssueReferenceNumber(
                        latestIssue.getIssueReferenceNumber()
                );

                dto.setLastIssuedDate(
                        latestIssue.getIssuedDate()
                );

                dto.setIssuedBy(

                        latestIssue.getIssuedBy() == null

                                ? "-"

                                : latestIssue
                                        .getIssuedBy()
                                        .getEmployeeName()
                );
            }
        }



        List<ReturnedItem> returnHistory =
                returnedItemRepository
                        .findByAssetItemAssetItemId(
                                asset.getAssetItemId()
                        );

        ReturnedItem latestReturn = null;

        if (!returnHistory.isEmpty()) {

            latestReturn =
                    returnHistory
                            .stream()
                            .max(
                                    Comparator.comparing(
                                            ReturnedItem::getReturnedDate
                                    )
                            )
                            .orElse(null);

            if (latestReturn != null) {

                dto.setLastReturnedDate(
                        latestReturn.getReturnedDate()
                );

                dto.setLatestReturnCondition(
                        latestReturn
                                .getReturnCondition()
                                .name()
                );
            }
        }



        IssuedItem latestIssue = issueHistory
                .stream()
                .max(
                        Comparator.comparing(
                                IssuedItem::getIssuedDate
                        )
                )
                .orElse(null);

        if (latestIssue == null) {

            dto.setCurrentHolder(
                    "AVAILABLE"
            );

            return dto;
        }

        boolean currentlyIssued;

        if (latestReturn == null) {

            currentlyIssued = true;

        } else {

            currentlyIssued = latestIssue
                    .getIssuedDate()
                    .isAfter(
                            latestReturn.getReturnedDate()
                    );
        }

        if (currentlyIssued) {

            dto.setCurrentHolder(

                    latestIssue.getIssuedToEmployee() == null

                            ? "ASSIGNED"

                            : latestIssue
                                    .getIssuedToEmployee()
                                    .getEmployeeName()
            );

        } else {

            dto.setCurrentHolder(
                    "AVAILABLE"
            );
        }

        return dto;
    }

    public List<IssuedItem> getIssueHistory(
        Long assetItemId
    ) {

        return issuedItemRepository
                .findByAssetItemAssetItemId(
                        assetItemId
                )
                .stream()

                .sorted(
                        Comparator.comparing(
                                IssuedItem::getIssuedDate
                        )
                        .reversed()
                )

                .toList();
    }

    public List<ReturnedItem> getReturnHistory(
            Long assetItemId
    ) {

        return returnedItemRepository
                .findByAssetItemAssetItemId(
                        assetItemId
                )
                .stream()

                .sorted(
                        Comparator.comparing(
                                ReturnedItem::getReturnedDate
                        )
                        .reversed()
                )

                .toList();
    }
}