package com.guard.vaultguard.config;

import com.guard.vaultguard.dto.users.UserRequest;
import com.guard.vaultguard.repositories.UserRepository;
import com.guard.vaultguard.service.UserService;
import jakarta.annotation.PreDestroy;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration
@Profile("dev") // runs only on dev mode
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
            System.out.println(CYAN + "========================================================" + RESET);
            System.out.println(CYAN + BOLD + "[VaultGuard] Starting Automated Local User Seeding..." + RESET);
            System.out.println(CYAN + "========================================================" + RESET);

            String logPattern = "Role: [%s%-12s%s] Username: '%s%-13s%s' Password: %-10s Status: %s%n";

            try {
                UserRequest userJake = new UserRequest();
                userJake.setUsername(USERNAME_1);
                userJake.setPassword("ja@123");
                userJake.setEmail("jake@vaultguard.com");
                authService.registerUser(userJake);

                System.out.printf(logPattern, YELLOW, "ROLE_USER", RESET, CYAN, USERNAME_1, RESET, "ja@123", GREEN + BOLD + "SEEDED" + RESET);
            } catch (Exception e) {
                System.out.printf(logPattern, YELLOW, "ROLE_USER", RESET, CYAN, USERNAME_1, RESET, "ja@123", RED + "SKIPPED (Exists)" + RESET);
            }

            try {
                UserRequest analystAlex = new UserRequest();
                analystAlex.setUsername(USERNAME_2);
                analystAlex.setPassword("alex@123");
                analystAlex.setEmail("alex@vaultguard.com");
                authService.registerUser(analystAlex);

                System.out.printf(logPattern, YELLOW, "ROLE_ANALYST", RESET, CYAN, USERNAME_2, RESET, "alex@123", GREEN + BOLD + "SEEDED" + RESET);
            } catch (Exception e) {
                System.out.printf(logPattern, YELLOW, "ROLE_ANALYST", RESET, CYAN, USERNAME_2, RESET, "alex@123", RED + "SKIPPED (Exists)" + RESET);
            }

            try {
                UserRequest managerMitch = new UserRequest();
                managerMitch.setUsername(USERNAME_3);
                managerMitch.setPassword("mitch@123");
                managerMitch.setEmail("mitch@vaultguard.com");
                authService.registerUser(managerMitch);

                System.out.printf(logPattern, YELLOW, "ROLE_MANAGER", RESET, CYAN, USERNAME_3, RESET, "mitch@123", GREEN + BOLD + "SEEDED" + RESET);
            } catch (Exception e) {
                System.out.printf(logPattern, YELLOW, "ROLE_MANAGER", RESET, CYAN, USERNAME_3, RESET, "mitch@123", RED + "SKIPPED (Exists)" + RESET);
            }

            System.out.println(CYAN + "========================================================" + RESET);
            System.out.println(GREEN + BOLD + "[VaultGuard] User Seeding Flow Complete!" + RESET);
            System.out.println(CYAN + "========================================================" + RESET);
        };
    }

    @PreDestroy
    public void tearDownDatabase() {
        System.out.println("\n" + RED + "========================================================" + RESET);
        System.out.println(RED + BOLD + "[VaultGuard] Graceful Shutdown Initiated..." + RESET);
        System.out.println(RED + "Clearing automated local test users from database..." + RESET);
        System.out.println(RED + "========================================================" + RESET);

        try {
            // Find them by your string constants and purge them cleanly
            userRepository.findByUsername(USERNAME_1).ifPresent(userRepository::delete);
            userRepository.findByUsername(USERNAME_2).ifPresent(userRepository::delete);
            userRepository.findByUsername(USERNAME_3).ifPresent(userRepository::delete);

            System.out.println(GREEN + BOLD + "Local test accounts securely purged from local instance!" + RESET);
        } catch (Exception e) {
            System.out.println(YELLOW + "Error removing seeded data: " + e.getMessage() + RESET);
        }

        System.out.println(RED + "========================================================" + RESET + "\n");
    }
}