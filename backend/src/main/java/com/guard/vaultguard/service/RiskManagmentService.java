package com.guard.vaultguard.service;

import com.guard.vaultguard.entities.enums.RiskLevel;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Service;


@Service
@NoArgsConstructor
public class RiskManagmentService {

    public RiskLevel getLevel(Double trxScore){
        if (trxScore >= 0.7) {
            return RiskLevel.HIGH;
        } else if (trxScore >= 0.5) {
            return RiskLevel.MEDIUM;
        } else {
            return RiskLevel.LOW;
        }
    }

}
