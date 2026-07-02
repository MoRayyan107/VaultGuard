package com.guard.vaultguard.config;

import com.guard.vaultguard.dto.users.UserRequest;
import com.guard.vaultguard.repositories.UserRepository;
import com.guard.vaultguard.service.UserService;
import jakarta.annotation.PreDestroy;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;


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

    public DataSeeder(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Bean
    CommandLineRunner initDatabase(UserService authService) {
        return args -> {
            long startTime = System.currentTimeMillis();
            int totalUsers = 0;
            StringBuilder logBuilder = new StringBuilder();

            // Build the header for log
            logBuilder.append('\n' + CYAN + "========================================================" + RESET + '\n')
                    .append(CYAN + BOLD + "[VaultGuard] Starting Automated Local User Seeding..." + RESET + '\n')
                    .append(CYAN + "========================================================" + RESET + '\n');

            // add the users into a array
            String[][] usersToSeed = {
                    {"ROLE_USER", USERNAME_1, "ja@123", "jake@vaultguard.com"},
                    {"ROLE_ANALYST", USERNAME_2, "alex@123", "alex@vaultguard.com"},
                    {"ROLE_MANAGER", USERNAME_3, "mitch@123", "mitch@vaultguard.com"}
            };

            // register the users
            for (String[] userData : usersToSeed) {
                String role = userData[0];
                String username = userData[1];
                String password = userData[2];
                String email = userData[3];
                boolean isSeeded = false;

                try {
                    UserRequest req = new UserRequest();
                    req.setUsername(username);
                    req.setPassword(password);
                    req.setEmail(email);
                    authService.registerUser(req);
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
                    .append(GREEN + "Total Users : " + totalUsers + '\n')
                    .append("Time Taken  : " + duration + "ms" + RESET + '\n')
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

        // Build the teardown banner as one string block
        logBuilder.append('\n' + RED + "========================================================" + RESET + '\n')
                .append(RED + BOLD + "[VaultGuard] Graceful Shutdown Initiated..." + RESET + '\n')
                .append(RED + "Clearing automated local test users from database..." + RESET + '\n')
                .append(RED + "========================================================" + RESET + '\n');

        if (!hasErrors) {
            logBuilder.append(GREEN + BOLD + "Local test accounts securely purged from local instance!" + RESET + '\n');
        } else {
            logBuilder.append(RED + BOLD + "Purge completed with some errors." + RESET + '\n');
        }

        logBuilder.append(RED + "========================================================" + RESET);

        // Output as ONE single log statement when CTRL+C is hit
        log.info("{}", logBuilder.toString());
    }
}