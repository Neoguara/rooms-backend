package com.neoguara.rooms.user.domain.entities;

import com.neoguara.rooms.shared.domain.exceptions.InvalidStateException;
import com.neoguara.rooms.user.domain.enums.UserRole;
import com.neoguara.rooms.user.domain.enums.UserStatus;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class UserDeleteRequiresInactiveTest {

    private User newUser() {
        return User.create("Maria", "maria@example.com", "senha-secreta", UserRole.USER);
    }

    @Test
    void userCannotBeDeletedWhileActive() {
        User user = newUser();
        InvalidStateException ex = assertThrows(InvalidStateException.class, user::delete);
        assertEquals("User must be inactive before deletion", ex.getMessage());
        assertEquals(UserStatus.ACTIVE, user.getStatus());
    }

    @Test
    void userIsDeletedOnceInactive() {
        User user = newUser();
        user.deactivate();
        user.delete();
        assertEquals(UserStatus.DELETED, user.getStatus());
    }

    @Test
    void deletedUserCannotBeReactivatedOrDeletedAgain() {
        User user = newUser();
        user.deactivate();
        user.delete();
        assertThrows(InvalidStateException.class, user::activate);
        assertThrows(InvalidStateException.class, user::delete);
        assertEquals(UserStatus.DELETED, user.getStatus());
    }
}
