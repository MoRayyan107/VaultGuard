package com.guard.vaultguard.config;

import com.guard.vaultguard.entities.Bank;
import com.guard.vaultguard.entities.RiskManagement;
import com.guard.vaultguard.entities.Transaction;
import com.guard.vaultguard.entities.Users;
import com.guard.vaultguard.entities.enums.RiskLevel;
import com.guard.vaultguard.entities.enums.TransactionStatus;
import com.guard.vaultguard.entities.enums.TransactionType;
import com.guard.vaultguard.entities.enums.UserRole;
import com.guard.vaultguard.repositories.BankRepository;
import com.guard.vaultguard.repositories.RiskManagmentRepository;
import com.guard.vaultguard.repositories.TransactionRepository;
import com.guard.vaultguard.repositories.UserRepository;
import jakarta.annotation.PreDestroy;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


// The logic was made by AI
@Configuration
@Profile("dev")
@Slf4j
public class DataSeeder {

    // colors for console output
    private final String RESET = "\u001B[0m";
    private final String CYAN = "\u001B[36m";
    private final String YELLOW = "\u001B[33m";
    private final String GREEN = "\u001B[32m";
    private final String RED = "\u001B[31m";
    private final String BOLD = "\u001B[1m";

    private static final String USERNAME_1 = "jake";
    private static final String USERNAME_2 = "alex_analyst";
    private static final String USERNAME_3 = "mitch_manager";

    private final UserRepository userRepository;
    private final BankRepository bankRepository;
    private final TransactionRepository transactionRepository;
    private final RiskManagmentRepository riskManagmentRepository;

    public DataSeeder(UserRepository userRepository,
                      BankRepository bankRepository,
                      TransactionRepository transactionRepository,
                      RiskManagmentRepository riskManagmentRepository) {
        this.userRepository = userRepository;
        this.bankRepository = bankRepository;
        this.transactionRepository = transactionRepository;
        this.riskManagmentRepository = riskManagmentRepository;
    }

    // a small record for representing the transaction data to be seeded
    private record SeedTransaction(
            String senderAccountNumber, String senderBankCode, String senderLocation,
            BigDecimal amount,
            String recipientAccountNumber, String recipientBankCode,
            TransactionType type, TransactionStatus status,
            int daysAgo, Double riskScore, RiskLevel riskLevel
    ) {}

    // add the users into a array
    String[][] usersToSeed = {
            {UserRole.USER.name(), USERNAME_1, "ja@123", "jake@vaultguard.com"},
            {UserRole.ANALYST.name(), USERNAME_2, "alex@123", "alex@vaultguard.com"},
            {UserRole.MANAGER.name(), USERNAME_3, "mitch@123", "mitch@vaultguard.com"}
    };

    String[][] banksToSeed = {
            {"ScotBank", "SCOTBANK", "true"},
            {"HSBC UK", "HSBCUK", "true"},
            {"Emirates NBD", "EMIRATESNBD", "true"},
            {"Legacy Trust Bank", "LEGACYTRUST", "false"}
    };

    SeedTransaction[] transactionsToSeed = {
            new SeedTransaction("ACC10001", "SCOTBANK", "Glasgow, UK", new BigDecimal("250.00"), "ACC20001", "HSBCUK", TransactionType.TRANSFER, TransactionStatus.COMPLETED, 9, 0.1, RiskLevel.LOW),
            new SeedTransaction("ACC10002", "HSBCUK", "London, UK", new BigDecimal("75.50"), "ACC20002", "SCOTBANK", TransactionType.DEPOSIT, TransactionStatus.COMPLETED, 8, 0.2, RiskLevel.LOW),
            new SeedTransaction("ACC10003", "SCOTBANK", "Bengaluru, IN", new BigDecimal("1200.00"), null, null, TransactionType.WITHDRAW, TransactionStatus.COMPLETED, 7, 0.3, RiskLevel.LOW),
            new SeedTransaction("ACC10004", "EMIRATESNBD", "Karachi, PK", new BigDecimal("630.75"), "ACC20004", "HSBCUK", TransactionType.WITHDRAW, TransactionStatus.FAILED, 6, 0.4, RiskLevel.MEDIUM),
            new SeedTransaction("ACC10005", "SCOTBANK", "Manchester, UK", new BigDecimal("42.00"), null, null, TransactionType.DEPOSIT, TransactionStatus.PENDING, 5, 0.5, RiskLevel.MEDIUM),
            new SeedTransaction("ACC10006", "HSBCUK", "Edinburgh, UK", new BigDecimal("88.20"), null, null, TransactionType.WITHDRAW, TransactionStatus.PENDING, 4, 0.6, RiskLevel.MEDIUM),
            new SeedTransaction("ACC10007", "EMIRATESNBD", "Dubai, UAE", new BigDecimal("5000.00"), "ACC20007", "SCOTBANK", TransactionType.TRANSFER, TransactionStatus.FLAGGED, 3, 0.7, RiskLevel.HIGH),
            new SeedTransaction("ACC10008", "LEGACYTRUST", "Lagos, NG", new BigDecimal("9800.00"), "ACC20008", "HSBCUK", TransactionType.TRANSFER, TransactionStatus.FLAGGED, 2, 0.8, RiskLevel.HIGH),
            new SeedTransaction("ACC10009", "EMIRATESNBD", "Dubai, UAE", new BigDecimal("15000.00"), "ACC20009", "SCOTBANK", TransactionType.TRANSFER, TransactionStatus.FLAGGED, 1, 0.9, RiskLevel.HIGH)
    };

    private final List<Transaction> seededTransactions = new ArrayList<>();
    private final List<RiskManagement> seededRiskManagement = new ArrayList<>();
    private final Map<String, Bank> bankMap = new HashMap<>();

    @Bean
    CommandLineRunner initDatabase(PasswordEncoder passwordEncoder) {
        return args -> {
            long startTime = System.currentTimeMillis();
            StringBuilder logBuilder = new StringBuilder();

            // Build the header for log
            logBuilder.append('\n' + CYAN + "========================================================" + RESET + '\n')
                    .append(CYAN + BOLD + "[VaultGuard] Starting Automated Local Seeding..." + RESET + '\n')
                    .append(CYAN + "========================================================" + RESET + '\n');


            logBuilder.append(CYAN + "=========================== [BANKS] =============================" + RESET + '\n');
            int totalBanks = 0;
            for (String[] bankData : banksToSeed) {
                String bankName = bankData[0];
                String bankCode = bankData[1];
                boolean active = Boolean.parseBoolean(bankData[2]);
                boolean isSeeded = false;

                try {
                    Bank savedBank = bankRepository.save(
                            Bank.builder()
                                    .bankName(bankName)
                                    .bankCode(bankCode)
                                    .active(active)
                                    .build()
                    );

                    totalBanks++;
                    bankMap.put(bankCode, savedBank);
                    isSeeded = true;
                } catch (Exception ignored) {
                    // ignore the exceptions
                }

                String paddedName = String.format("%-20s", "'" + bankName + "'");
                String paddedCode = String.format("%-14s", bankCode);
                String status = isSeeded ? (GREEN + BOLD + "SEEDED" + RESET) : (RED + "SKIPPED (Exists)" + RESET);

                logBuilder.append(String.format("Bank: %s%s%s Code: [%s%s%s] Active: %-5s Status: %s%n",
                        CYAN, paddedName, RESET,
                        YELLOW, paddedCode, RESET,
                        active, status));
            }

            /// ----------------------------------------------------------------------------------
            // seed the transactions + their risk management rows
            logBuilder.append(CYAN + "=========================== [TRANSACTIONS] =============================" + RESET + '\n');
            int totalTransactions = 0;
            for (SeedTransaction t : transactionsToSeed) {
                boolean isSeeded = false;

                try {
                    LocalDateTime txDate = LocalDateTime.now().minusDays(t.daysAgo());

                    Transaction transaction = Transaction.builder()
                            .senderAccountNumber(t.senderAccountNumber())
                            .senderBank(bankMap.get(t.senderBankCode()))
                            .senderLocation(t.senderLocation())
                            .amount(t.amount())
                            .recipientAccountNumber(t.recipientAccountNumber())
                            .recipientBank(t.recipientBankCode() != null ? bankMap.get(t.recipientBankCode()) : null)
                            .transactionType(t.type())
                            .transactionDate(txDate)
                            .build();

                    Transaction savedTransaction = transactionRepository.save(transaction);
                    seededTransactions.add(savedTransaction);

                    RiskManagement risk = RiskManagement.builder()
                            .transaction(savedTransaction)
                            .riskScore(t.riskScore())
                            .riskLevel(t.riskLevel())
                            .transactionStatus(t.status())
                            .createdAt(txDate)
                            .build();

                    RiskManagement savedRisk = riskManagmentRepository.save(risk);
                    seededRiskManagement.add(savedRisk);

                    totalTransactions++;
                    isSeeded = true;
                } catch (Exception ignored) {
                    // Ignored — visualized via status column below
                }

                String status = isSeeded ? (GREEN + BOLD + "SEEDED" + RESET) : (RED + "SKIPPED" + RESET);
                logBuilder.append(String.format("Txn: %s%-10s%s -> %-10s Amount: %s£%-10s Status: %s%n",
                        CYAN, t.senderAccountNumber(), RESET,
                        t.recipientAccountNumber() != null ? t.recipientAccountNumber() : "N/A",
                        GREEN, t.amount(), status));
            }


            // -----------------------------------------------------------------------------------
            // register the users
            logBuilder.append(CYAN + "=========================== [USERS] =============================" + RESET + '\n');
            int totalUsers = 0;
            for (String[] userData : usersToSeed) {
                UserRole role = UserRole.valueOf(userData[0]);
                String username = userData[1];
                String password = userData[2];
                String email = userData[3];
                boolean isSeeded = false;

                try {
                    Users user = Users.builder()
                            .username(username)
                            .password(passwordEncoder.encode(password))
                            .email(email)
                            .role(role)
                            .build();

                    userRepository.save(user);
                    totalUsers++;
                    isSeeded = true;
                } catch (Exception ignored) {
                    // Ignored because we handle the failure visually in the log below
                }

                // Formatted Lines with spaces
                String paddedRole = String.format("%-14s", role);
                String paddedUsername = String.format("%-16s", "'" + username + "'");
                String paddedPassword = String.format("%-12s", password);
                String status = isSeeded ? (GREEN + BOLD + "SEEDED" + RESET) : (RED + "SKIPPED (Exists)" + RESET);

                // Append the row to our single log block
                logBuilder.append(String.format("Role: [%s%s%s] Username: %s%s%s Password: %s Status: %s%n",
                        YELLOW, paddedRole, RESET,
                        CYAN, paddedUsername, RESET,
                        paddedPassword, status));
            }

            long duration = System.currentTimeMillis() - startTime;

            // 4. Build Footer
            logBuilder.append(CYAN+"========================================================"+RESET + '\n')
                    .append(GREEN + BOLD+ "[VaultGuard] User Seeding Flow Complete!" + RESET + '\n')
                    .append(GREEN + "Total Users         : " + totalUsers + '\n')
                    .append(GREEN + "Banks Seeded        : " + totalBanks + '\n')
                    .append(GREEN + "Transactions Seeded : " + totalTransactions + '\n')
                    .append(GREEN + "Time Taken          : " + duration + "ms" + RESET + '\n')
                    .append(CYAN + "========================================================" + RESET);

            /// after all that put the log
            log.info("{}", logBuilder.toString());
        };
    }

    @PreDestroy
    public void tearDownDatabase() {
        StringBuilder logBuilder = new StringBuilder();
        boolean hasErrors = false;

        try {
            userRepository.findByUsername(USERNAME_1).ifPresent(userRepository::delete);
            userRepository.findByUsername(USERNAME_2).ifPresent(userRepository::delete);
            userRepository.findByUsername(USERNAME_3).ifPresent(userRepository::delete);
        } catch (Exception e) {
            hasErrors = true;
            logBuilder.append(YELLOW + "Error removing seeded data: " + e.getMessage() + RESET + '\n');
        }

        // RiskManagement must be deleted BEFORE Transaction — transaction_id FK is
        // NOT NULL on risk_management, so a Transaction row can't be removed while
        // a RiskManagement row still references it (same dependency direction as
        // banks-before-transactions on the way in, just reversed for teardown).
        try {
            riskManagmentRepository.deleteAll(seededRiskManagement);
        } catch (Exception e) {
            hasErrors = true;
            logBuilder.append(YELLOW + "Error removing seeded risk management: " + e.getMessage() + RESET + '\n');
        }

        try {
            transactionRepository.deleteAll(seededTransactions);
        } catch (Exception e) {
            hasErrors = true;
            logBuilder.append(YELLOW + "Error removing seeded transactions: " + e.getMessage() + RESET + '\n');
        }


        try{
            for (int i = 0; i < bankMap.size(); i++) {
                bankRepository.delete(bankMap.get(banksToSeed[i][1]));
            }
        } catch (Exception e) {
            hasErrors = true;
            logBuilder.append(YELLOW + "Error removing seeded banks: " + e.getMessage() + RESET + '\n');
        }

        // Build the teardown banner as one string block
        logBuilder.append('\n' + RED + "========================================================" + RESET + '\n')
                .append(RED + BOLD + "[VaultGuard] Graceful Shutdown Initiated..." + RESET + '\n')
                .append(RED + "Clearing automated local test data from database..." + RESET + '\n')
                .append(RED + "========================================================" + RESET + '\n');

        if (!hasErrors) {
            logBuilder.append(GREEN + BOLD + "Local test data securely purged from local instance!" + RESET + '\n');
        } else {
            logBuilder.append(RED + BOLD + "Purge completed with some errors." + RESET + '\n');
        }

        logBuilder.append(RED + "========================================================" + RESET);

        // Output as ONE single log statement when CTRL+C is hit
        log.info("{}", logBuilder.toString());
    }
}