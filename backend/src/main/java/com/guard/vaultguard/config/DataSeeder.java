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

    private static final String USERNAME_1 = "alex_analyst";
    private static final String USERNAME_2 = "mitch_manager";

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
            int daysAgo, Double riskScore, RiskLevel riskLevel,
            String transactionReference
    ) {}

    private static SeedTransaction[] buildTransactionsToSeed() {
        List<SeedTransaction> seeds = new ArrayList<>();
        String[] senderBanks = {"SCOTBANK", "HSBCUK", "EMIRATESNBD", "LEGACYTRUST"};
        String[] recipientBanks = {"HSBCUK", "SCOTBANK", "EMIRATESNBD", "LEGACYTRUST"};
        String[] locations = {
                "Glasgow, UK", "London, UK", "Bengaluru, IN", "Karachi, PK",
                "Manchester, UK", "Edinburgh, UK", "Dubai, UAE", "Lagos, NG",
                "Abu Dhabi, UAE", "Birmingham, UK", "Chennai, IN", "Islamabad, PK"
        };

        for (int i = 1; i <= 50; i++) {
            String senderBankCode = senderBanks[(i - 1) % senderBanks.length];
            TransactionType type = switch (i % 3) {
                case 1 -> TransactionType.TRANSFER;
                case 2 -> TransactionType.DEPOSIT;
                default -> TransactionType.WITHDRAW;
            };

            TransactionStatus status = switch (i % 4) {
                case 1 -> TransactionStatus.COMPLETED;
                case 2 -> TransactionStatus.PENDING;
                case 3 -> TransactionStatus.FAILED;
                default -> TransactionStatus.FLAGGED;
            };

            RiskLevel riskLevel = switch (status) {
                case COMPLETED -> RiskLevel.LOW;
                case PENDING -> RiskLevel.MEDIUM;
                case FAILED, FLAGGED -> RiskLevel.HIGH;
            };

            Double riskScore = switch (riskLevel) {
                case LOW -> 0.15;
                case MEDIUM -> 0.45;
                case HIGH -> 0.85;
            };

            String recipientAccountNumber = type == TransactionType.TRANSFER ? String.format("ACC2%04d", i) : null;
            String recipientBankCode = type == TransactionType.TRANSFER
                    ? recipientBanks[(i + 1) % recipientBanks.length]
                    : null;

            BigDecimal amount = new BigDecimal(String.format("%d.%02d", 25 + ((i * 137) % 9750), (i * 17) % 100));

            seeds.add(new SeedTransaction(
                    String.format("ACC1%04d", i),
                    senderBankCode,
                    locations[(i - 1) % locations.length],
                    amount,
                    recipientAccountNumber,
                    recipientBankCode,
                    type,
                    status,
                    50 - i,
                    riskScore,
                    riskLevel,
                    String.format("%s-REF-%04d", senderBankCode, i)
            ));
        }

        return seeds.toArray(new SeedTransaction[0]);
    }

    // add the users into a array
    String[][] usersToSeed = {
            {UserRole.ANALYST.name(), USERNAME_1, "alex@123", "alex@vaultguard.com"},
            {UserRole.MANAGER.name(), USERNAME_2, "mitch@123", "mitch@vaultguard.com"}
    };

    String[][] banksToSeed = {
            {"ScotBank", "SCOTBANK", "true"},
            {"HSBC UK", "HSBCUK", "true"},
            {"Emirates NBD", "EMIRATESNBD", "true"},
            {"Legacy Trust Bank", "LEGACYTRUST", "false"}
    };

    SeedTransaction[] transactionsToSeed = buildTransactionsToSeed();

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


            logBuilder.append(CYAN + "=========================== [SEEDING BANKS] =============================" + RESET + '\n');
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
            logBuilder.append(CYAN + "=========================== [SEEDING TRANSACTIONS] =============================" + RESET + '\n');
            int totalTransactions = 0;
            boolean isTrxSeeded = false;
            for (SeedTransaction t : transactionsToSeed) {
                isTrxSeeded = false;

                try {
                    LocalDateTime txDate = LocalDateTime.now().minusDays(t.daysAgo());

                    Transaction transaction = Transaction.builder()
                            .senderAccountNumber(t.senderAccountNumber())
                            .senderBank(bankMap.get(t.senderBankCode()))
                            .senderLocation(t.senderLocation())
                            .transactionReference(t.transactionReference())
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
                    isTrxSeeded = true;
                } catch (Exception ignored) {
                    // Ignored — visualized via status column below
                }
            }
            String trxStatus = isTrxSeeded ? (GREEN + BOLD + "SEEDED" + RESET) : (RED + "SKIPPED" + RESET);
            logBuilder.append(GREEN+"Transaction "+trxStatus + RESET + "\n");


            // -----------------------------------------------------------------------------------
            // register the users
            logBuilder.append(CYAN + "=========================== [SEEDING USERS] =============================" + RESET + '\n');
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
                    .append(GREEN).append("Total Users         : ").append(totalUsers).append('\n')
                    .append(GREEN).append("Banks Seeded        : ").append(totalBanks).append('\n')
                    .append(GREEN).append("Transactions Seeded : ").append(totalTransactions).append('\n')
                    .append(GREEN).append("Time Taken          : ").append(duration).append("ms").append(RESET).append('\n')
                    .append(CYAN + "========================================================" + RESET);

            /// after all that put the log
            log.info("{}", logBuilder);
        };
    }

    @PreDestroy
    public void tearDownDatabase() {
        StringBuilder logBuilder = new StringBuilder();
        boolean hasErrors = false;

        try {
            userRepository.findByUsername(USERNAME_1).ifPresent(userRepository::delete);
            userRepository.findByUsername(USERNAME_2).ifPresent(userRepository::delete);
        } catch (Exception e) {
            hasErrors = true;
            logBuilder.append(YELLOW).append("Error removing seeded data: ").append(e.getMessage()).append(RESET).append('\n');
        }

        // RiskManagement must be deleted BEFORE Transaction — transaction_id FK is
        // NOT NULL on risk_management, so a Transaction row can't be removed while
        // a RiskManagement row still references it (same dependency direction as
        // banks-before-transactions on the way in, just reversed for teardown).
        try {
            riskManagmentRepository.deleteAll(seededRiskManagement);
        } catch (Exception e) {
            hasErrors = true;
            logBuilder.append(YELLOW).append("Error removing seeded risk management: ").append(e.getMessage()).append(RESET).append('\n');
        }

        try {
            transactionRepository.deleteAll(seededTransactions);
        } catch (Exception e) {
            hasErrors = true;
            logBuilder.append(YELLOW).append("Error removing seeded transactions: ").append(e.getMessage()).append(RESET).append('\n');
        }


        try{
            for (int i = 0; i < bankMap.size(); i++) {
                bankRepository.delete(bankMap.get(banksToSeed[i][1]));
            }
        } catch (Exception e) {
            hasErrors = true;
            logBuilder.append(YELLOW).append("Error removing seeded banks: ").append(e.getMessage()).append(RESET).append('\n');
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
        log.info("{}", logBuilder);
    }
}