package com.guard.vaultguard.repositories;

import com.guard.vaultguard.entities.RiskManagement;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface RiskManagmentRepository extends JpaRepository<RiskManagement, UUID> {

    boolean existsByTransaction_Id(UUID transactionId);

}
