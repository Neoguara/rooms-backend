package com.neoguara.rooms.user.domain.entities;

import com.neoguara.rooms.shared.domain.validation.Notification;
import com.neoguara.rooms.user.domain.enums.UserRole;
import com.neoguara.rooms.user.domain.valueobjects.UserId;
import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "users")
public class User {

    @EmbeddedId
    private UserId id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String password;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private UserRole role;

    private Boolean isActive;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime deletedAt;

    protected User() {}

    private User(String name, String email, String password, UserRole role) {
        this.id = new UserId();
        this.name = name;
        this.email = email;
        this.password = password;
        this.role = role;
        this.createdAt = LocalDateTime.now();
        this.isActive = true;
    }

    public static User create(String name, String email, String password, UserRole role) {
        Notification notification = Notification.create()
                .addErrorIf(name == null || name.isBlank(), "name is required")
                .addErrorIf(email == null || email.isBlank(), "email is required")
                .addErrorIf(password == null || password.isBlank(), "password is required")
                .addErrorIf(role == null, "role is required");
        notification.raiseIfHasErrors();
        return new User(name, email, password, role);
    }

    public void update(String name, String email, String password, UserRole role, Boolean isActive) {
        this.name = name;
        this.email = email;
        this.password = password;
        this.role = role;
        this.isActive = isActive;
        this.updatedAt = LocalDateTime.now();
    }

    public void softDelete() {
        this.isActive = false;
        this.deletedAt = LocalDateTime.now();
    }

    public UserId getId() { return id; }
    public String getName() { return name; }
    public String getEmail() { return email; }
    public String getPassword() { return password; }
    public UserRole getRole() { return role; }
    public Boolean getActive() {return isActive;}

    public LocalDateTime getCreatedAt() {return createdAt;}
    public LocalDateTime getUpdatedAt() {return updatedAt;}
    public LocalDateTime getDeletedAt() {return deletedAt;}
}
