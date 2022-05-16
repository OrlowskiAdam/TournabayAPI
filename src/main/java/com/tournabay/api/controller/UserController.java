package com.tournabay.api.controller;

import com.tournabay.api.exception.BadRequestException;
import com.tournabay.api.model.User;
import com.tournabay.api.security.CurrentUser;
import com.tournabay.api.security.UserPrincipal;
import com.tournabay.api.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class UserController {
    private final UserService userService;

    /**
     * Get the current user from the database using the userPrincipal object
     *
     * @param userPrincipal The userPrincipal object is the object that contains the user's information.
     * @return A user object
     */
    @GetMapping("/user/me")
    public ResponseEntity<User> getCurrentUser(@CurrentUser UserPrincipal userPrincipal) {
        if (userPrincipal == null) throw new BadRequestException("Token expired");
        User user = userService.getUserFromPrincipal(userPrincipal);
        return ResponseEntity.ok(user);
    }
}
