package com.neoguara.rooms.user.domain.entities;

import com.neoguara.rooms.shared.domain.exceptions.InvalidStateException;
import com.neoguara.rooms.shared.domain.validation.Notification;
import com.neoguara.rooms.user.domain.enums.UserRole;
import com.neoguara.rooms.user.domain.enums.UserStatus;
import com.neoguara.rooms.user.domain.validation.UserValidator;
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

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private UserStatus status;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    protected User() {}

    private User(String name, String email, String password, UserRole role) {
        this.id = new UserId();
        this.name = name;
        this.email = email;
        this.password = password;
        this.role = role;
        this.status = UserStatus.ACTIVE;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    public static User create(String name, String email, String password, UserRole role) {
        User user = new User(name, email, password, role);
        Notification notification = Notification.create();
        new UserValidator().validate(user, notification);
        notification.raiseIfHasErrors();
        return user;
    }

    public void update(String name, String email, String password, UserRole role) {
        this.name = name;
        this.email = email;
        this.password = password;
        this.role = role;
        Notification notification = Notification.create();
        new UserValidator().validate(this, notification);
        notification.raiseIfHasErrors();
        this.updatedAt = LocalDateTime.now();
    }

    public void activate() {
        if (status == UserStatus.DELETED) throw new InvalidStateException("Deleted user cannot be modified");
        this.status = UserStatus.ACTIVE;
    }

    public void deactivate() {
        if (status == UserStatus.DELETED) throw new InvalidStateException("Deleted user cannot be modified");
        this.status = UserStatus.INACTIVE;
    }

    public void delete() {
        if (status != UserStatus.INACTIVE) throw new InvalidStateException("User must be inactive before deletion");
        this.status = UserStatus.DELETED;
    }

    public UserId getId() { return id; }
    public String getName() { return name; }
    public String getEmail() { return email; }
    public String getPassword() { return password; }
    public UserRole getRole() { return role; }
    public UserStatus getStatus() { return status; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
}
