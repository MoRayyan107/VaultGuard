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

@Configuration
@Profile("dev") // runs only on dev mode
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
            log.info('\n' + CYAN + "========================================================" + RESET + '\n' +
                    CYAN + BOLD + "[VaultGuard] Starting Automated Local User Seeding..." + RESET + '\n' +
                    CYAN + "========================================================" + RESET);

            long startTime = System.currentTimeMillis();
            int totalUsers = 0;

            try {
                UserRequest userJake = new UserRequest();
                userJake.setUsername(USERNAME_1);
                userJake.setPassword("ja@123");
                userJake.setEmail("jake@vaultguard.com");
                authService.registerUser(userJake);
                totalUsers++;

                printSeederLine("ROLE_USER", USERNAME_1, "ja@123", true);
            } catch (Exception e) {
                printSeederLine("ROLE_USER", USERNAME_1, "ja@123", false);
            }

            try {
                UserRequest analystAlex = new UserRequest();
                analystAlex.setUsername(USERNAME_2);
                analystAlex.setPassword("alex@123");
                analystAlex.setEmail("alex@vaultguard.com");
                authService.registerUser(analystAlex);
                totalUsers++;

                printSeederLine("ROLE_ANALYST", USERNAME_2, "alex@123", true);
            } catch (Exception e) {
                printSeederLine("ROLE_ANALYST", USERNAME_2, "alex@123", false);
            }

            try {
                UserRequest managerMitch = new UserRequest();
                managerMitch.setUsername(USERNAME_3);
                managerMitch.setPassword("mitch@123");
                managerMitch.setEmail("mitch@vaultguard.com");
                authService.registerUser(managerMitch);
                totalUsers++;

                printSeederLine("ROLE_MANAGER", USERNAME_3, "mitch@123", true);
            } catch (Exception e) {
                printSeederLine("ROLE_MANAGER", USERNAME_3, "mitch@123", false);
            }

            long duration = System.currentTimeMillis() - startTime;

            log.info('\n' + CYAN + "========================================================" + RESET + '\n' +
                    GREEN + BOLD + "[VaultGuard] User Seeding Flow Complete!" + RESET + '\n' +
                    GREEN + "Users: {}, \ntime taken: {}ms" + RESET + '\n' +
                    CYAN + "========================================================" + RESET,
                    totalUsers, duration);
        };
    }

    @PreDestroy
    public void tearDownDatabase() {
        log.info("[INFO] " + RED + "SHUTDOWN INITIATED" + RESET);
        try {
            // Find them by your string constants and purge them cleanly
            userRepository.findByUsername(USERNAME_1).ifPresent(userRepository::delete);
            userRepository.findByUsername(USERNAME_2).ifPresent(userRepository::delete);
            userRepository.findByUsername(USERNAME_3).ifPresent(userRepository::delete);

        } catch (Exception e) {
            log.error(YELLOW + "Error removing seeded data: {}" + RESET, e.getMessage());
        }
        log.info("\n" + RED + "========================================================" + RESET + '\n' +
                RED + BOLD + "[VaultGuard] Graceful Shutdown Initiated..." + RESET + '\n' +
                RED + "Clearing automated local test users from database..." + RESET + '\n' +
                RED + "========================================================" + RESET + "\n" +
                GREEN + BOLD + "Local test accounts securely purged from local instance!" + RESET + '\n' +
                RED + "========================================================" + RESET + "\n");
    }


    private void printSeederLine(String role, String username, String password, boolean isSeeded) {
        // Standardize widths using pure text padding (ignoring color codes)
        String paddedRole = String.format("%-14s", role);
        String paddedUsername = String.format("'%s'", username);
        paddedUsername = String.format("%-16s", paddedUsername);
        String paddedPassword = String.format("%-12s", password);

        String status = isSeeded ? GREEN + BOLD + "SEEDED" + RESET : RED + "SKIPPED (Exists)" + RESET;

        // Print cleanly with colors inserted around perfectly pre-spaced values
        System.out.printf("Role: [%s%s%s] Username: %s%s%s Password: %s Status: %s%n",
                YELLOW, paddedRole, RESET,
                CYAN, paddedUsername, RESET,
                paddedPassword,
                status
        );
    }

}