package com.guard.vaultguard.service;

import com.guard.vaultguard.entities.Bank;
import com.guard.vaultguard.exceptions.BankCodeNotFoundException;
import com.guard.vaultguard.exceptions.BankNotActiveException;
import com.guard.vaultguard.repositories.BankRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BankService {

    private final BankRepository bankRepository;

    public BankService(BankRepository bankRepository) {
        this.bankRepository = bankRepository;
    }

    public Bank getBankByCode(String bankCode) {
        Bank savedBank = bankRepository.findByBankCode(bankCode)
                .orElseThrow(() -> new BankCodeNotFoundException("Bank with code " + bankCode + " not found"));

        if (!savedBank.isActive()) throw new BankNotActiveException("Bank with code " + bankCode + " is not active");
        return savedBank;
    }

    public List<Bank> getActiveBanks(boolean status) {
        return bankRepository.findByActive(status);
    }
}
