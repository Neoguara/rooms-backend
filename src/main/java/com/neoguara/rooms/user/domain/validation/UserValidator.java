package com.neoguara.rooms.user.domain.validation;

import com.neoguara.rooms.shared.domain.validation.Notification;
import com.neoguara.rooms.shared.domain.validation.Validator;
import com.neoguara.rooms.user.domain.entities.User;

public class UserValidator implements Validator<User> {

    @Override
    public void validate(User target, Notification notification) {
        notification
                .addErrorIf(target.getName() == null || target.getName().isBlank(), "name is required")
                .addErrorIf(target.getEmail() == null || target.getEmail().isBlank(), "email is required")
                .addErrorIf(target.getPassword() == null || target.getPassword().isBlank(), "password is required")
                .addErrorIf(target.getRole() == null, "role is required");
    }
}
