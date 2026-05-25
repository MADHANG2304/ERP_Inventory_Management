package com.example.service;

import com.example.dto.InventoryTransactionDTO;
import com.example.dto.InventoryTransactionFilterDTO;
import com.example.entity.InventoryTransaction;
import com.example.repository.InventoryTransactionRepository;
import com.example.specification.InventoryTransactionSpecification;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class InventoryTransactionService {

    private final InventoryTransactionRepository
            inventoryTransactionRepository;

    public InventoryTransactionService(
            InventoryTransactionRepository inventoryTransactionRepository
    ) {

        this.inventoryTransactionRepository =
                inventoryTransactionRepository;
    }

    public List<InventoryTransactionDTO>
    getAllTransactions() {

        return inventoryTransactionRepository
                .findAll()
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public List<InventoryTransactionDTO>
    searchTransactions(
            String keyword
    ) {

        Specification<InventoryTransaction>
                specification =

                InventoryTransactionSpecification
                        .searchTransaction(keyword);

        return inventoryTransactionRepository
                .findAll(specification)
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public List<InventoryTransactionDTO>
                filterTransactions(
                        InventoryTransactionFilterDTO filterDTO
                ) {

                Specification<InventoryTransaction>
                        specification =

                        InventoryTransactionSpecification
                                .hasItemName(
                                        filterDTO.getItemName()
                                )

                                .and(

                                        InventoryTransactionSpecification
                                                .hasTransactionType(
                                                        filterDTO.getTransactionType()
                                                )
                                )

                                .and(

                                        InventoryTransactionSpecification
                                                .hasReferenceType(
                                                        filterDTO.getReferenceType()
                                                )
                                )

                                .and(

                                        InventoryTransactionSpecification
                                                .hasReferenceNumber(
                                                        filterDTO.getReferenceNumber()
                                                )
                                );

                return inventoryTransactionRepository
                        .findAll(specification)
                        .stream()
                        .map(this::convertToDTO)
                        .toList();
        }

    private InventoryTransactionDTO convertToDTO(
        InventoryTransaction transaction
                ) {

                InventoryTransactionDTO dto =
                        new InventoryTransactionDTO();

                dto.setTransactionId(
                        transaction.getTransactionId()
                );

                dto.setItemId(
                        transaction.getItem()
                                .getItemId()
                );

                dto.setItemName(
                        transaction.getItem()
                                .getItemName()
                );

                dto.setTransactionType(
                        transaction.getTransactionType()
                );

                dto.setReferenceType(
                        transaction.getReferenceType()
                );

                dto.setQuantity(
                        transaction.getQuantity()
                );

                dto.setRemarks(
                        transaction.getRemarks()
                );

                dto.setReferenceNumber(

                        transaction.getReferenceNumber() != null

                                ? transaction.getReferenceNumber()

                                : "-"
                );

                dto.setTransactionDate(
                        transaction.getTransactionDate()
                );

                return dto;
        }
}