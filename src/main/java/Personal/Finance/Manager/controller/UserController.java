package Personal.Finance.Manager.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import Personal.Finance.Manager.dto.request.ChangePasswordRequest;
import Personal.Finance.Manager.dto.request.UpdateUserRequest;
import Personal.Finance.Manager.dto.response.UserResponse;
import Personal.Finance.Manager.model.User;
import Personal.Finance.Manager.repository.UserRepository;
import Personal.Finance.Manager.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final UserRepository userRepository;

    @GetMapping("/me")
    public ResponseEntity<UserResponse> getCurrentUser(
            Authentication authentication) {

        User user = getAuthenticatedUser(authentication);

        return ResponseEntity.ok(
                userService.getCurrentUser(user)
        );
    }

    @PutMapping("/me")
    public ResponseEntity<UserResponse> updateCurrentUser(
            Authentication authentication,
            @Valid @RequestBody UpdateUserRequest request) {

        User user = getAuthenticatedUser(authentication);

        return ResponseEntity.ok(
                userService.updateCurrentUser(user, request)
        );
    }

    @PutMapping("/me/password")
    public ResponseEntity<String> changePassword(
            Authentication authentication,
            @Valid @RequestBody ChangePasswordRequest request) {

        User user = getAuthenticatedUser(authentication);

        userService.changePassword(user, request);

        return ResponseEntity.ok(
                "Password changed successfully"
        );
    }

    private User getAuthenticatedUser(
            Authentication authentication) {

        return userRepository.findByEmail(authentication.getName())
                .orElseThrow(() ->
                        new RuntimeException("User not found")
                );
    }
}