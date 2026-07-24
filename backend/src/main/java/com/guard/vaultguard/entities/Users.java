package com.guard.vaultguard.entities;

import com.guard.vaultguard.entities.enums.UserRole;
import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class Users {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID userId;

    @Column(nullable = false, unique = true)
    private String username;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private UserRole role;

    @Column(nullable = false, unique = true)
    private String email;

    @Override
    public String toString() {
        return "Users{" +
                ", userId=" + userId +
                ", username='" + username + '\'' +
                ", role=" + role +
                '}';
    }
}
