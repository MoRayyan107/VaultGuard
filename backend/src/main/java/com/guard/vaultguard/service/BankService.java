package com.guard.vaultguard.service;

import com.guard.vaultguard.dto.bank.BankRequest;
import com.guard.vaultguard.entities.Bank;
import com.guard.vaultguard.exceptions.BankCodeNotFoundException;
import com.guard.vaultguard.exceptions.BankNotActiveException;
import com.guard.vaultguard.repositories.BankRepository;
import com.guard.vaultguard.security.util.ApiFilterUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.Optional;

import static com.guard.vaultguard.security.util.ApiFilterUtil.generateApiKey;

@Service
@Slf4j
public class BankService {

    private final BankRepository bankRepository;

    @Value("${bank.api.key.starter}")
    private String bankApiKeyStarter;

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

    public Optional<Bank> getBankByApiKey(String apiKey) {
        return bankRepository.findBankByApiKey(apiKey);
    }

    public Bank registerBank(BankRequest request) throws NoSuchAlgorithmException {
        // checks of NotBlnak will throw if any were missing
        // buld the bank

        String bankApiKey = generateApiKey(bankApiKeyStarter);
        String hashedApiKey = ApiFilterUtil.hashApiKey(ApiFilterUtil.stripPrefix(bankApiKey, bankApiKeyStarter));

        Bank newBank = Bank.builder()
                .bankCode(request.getBankCode())
                .bankName(request.getBankName())
                .apiKey(hashedApiKey)
                .active(true)
                .build();

        Bank savedBank;
        try{
            savedBank = bankRepository.save(newBank);
        } catch (DataIntegrityViolationException ex){
            // TODO: MAKE A GLOBAL EXCEPTION HANDLER FOR THIS
            throw new DataIntegrityViolationException("Bank with code " + request.getBankCode() + " already exists");
        }

        // reset the api key to the original value with the prefix for returning to the user
        savedBank.setApiKey(bankApiKey);
        return savedBank;
    }
}
