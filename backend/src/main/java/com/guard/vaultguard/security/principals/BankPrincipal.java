package com.guard.vaultguard.security.principals;


import com.guard.vaultguard.entities.Bank;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.Collection;
import java.util.Collections;
import java.util.UUID;

public class BankPrincipal {

    private final Bank bank;

    public BankPrincipal(Bank bank) {
        this.bank = bank;
    }

    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.singleton(new SimpleGrantedAuthority("ROLE_BANK"));
    }

    public UUID getBankId() {
        return bank.getBankId();
    }

    public String getBankName() {
        return bank.getBankName();
    }

    public boolean isActiveBank() {
        return bank.isActive();
    }

    public String getBankCode(){
        return bank.getBankCode();
    }

}
