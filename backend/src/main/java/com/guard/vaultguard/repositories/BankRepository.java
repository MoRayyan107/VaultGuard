package com.guard.vaultguard.repositories;

import com.guard.vaultguard.entities.Bank;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface BankRepository extends JpaRepository<Bank, UUID> {

    Optional<Bank> findByBankCode(String bankCode);

    List<Bank> findByActive(boolean active);

}
