package com.guard.vaultguard.controllers;

import com.guard.vaultguard.dto.bank.BankRequest;
import com.guard.vaultguard.entities.Bank;
import com.guard.vaultguard.exceptions.IllegalTransactionTypeException;
import com.guard.vaultguard.service.BankService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.security.NoSuchAlgorithmException;
import java.util.List;

@PreAuthorize("hasRole('MANAGER') OR hasRole('ANALYST')")
@RestController
@RequestMapping("api/v1/bank")
public class BankController {

    private final BankService bankService;

    public BankController(BankService bankService) {
        this.bankService = bankService;
    }

    @GetMapping("/activeBanks")
    public ResponseEntity<List<Bank>> getActiveBanks() {
        List<Bank> activeBanks = getBankBasedOnStatus(true);
        return ResponseEntity.ok(activeBanks);
    }

    @GetMapping("/deactivatedBanks")
    public ResponseEntity<List<Bank>> getDeactivatedBanks() {
        List<Bank> deactivatedBanks = getBankBasedOnStatus(false);
        return ResponseEntity.ok(deactivatedBanks);
    }

    @PostMapping("/register")
    public ResponseEntity<Bank> registerBank(@RequestBody @Valid BankRequest bank) {
        try {
            Bank registeredBank = bankService.registerBank(bank);
            return ResponseEntity.ok(registeredBank);
        } catch (NoSuchAlgorithmException e) {
            // TODO: Log the exception and handle it appropriately
            throw new IllegalTransactionTypeException("Error occurred while generating API key");
        }
    }

    private List<Bank> getBankBasedOnStatus(boolean status) {
        return bankService.getActiveBanks(status);
    }
}
