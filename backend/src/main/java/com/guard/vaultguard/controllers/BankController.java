package com.guard.vaultguard.controllers;

import com.guard.vaultguard.entities.Bank;
import com.guard.vaultguard.service.BankService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@PreAuthorize("hasRole('ROLE_MANAGER') OR hasRole('ROLE_ANALYST')")
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

    private List<Bank> getBankBasedOnStatus(boolean status) {
        return bankService.getActiveBanks(status);
    }
}
