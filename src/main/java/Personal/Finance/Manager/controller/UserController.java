package Personal.Finance.Manager.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import Personal.Finance.Manager.dto.request.ChangePasswordRequest;
import Personal.Finance.Manager.dto.request.UpdateUserRequest;
import Personal.Finance.Manager.dto.response.UserResponse;
import Personal.Finance.Manager.model.User;
import Personal.Finance.Manager.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("/me")
    public ResponseEntity<UserResponse> getCurrentUser(
            @RequestAttribute("user") User user) {

        return ResponseEntity.ok(
                userService.getCurrentUser(user)
        );
    }

    @PutMapping("/me")
    public ResponseEntity<UserResponse> updateCurrentUser(
            @RequestAttribute("user") User user,
            @Valid @RequestBody UpdateUserRequest request) {

        return ResponseEntity.ok(
                userService.updateCurrentUser(
                        user,
                        request
                )
        );
    }

    @PutMapping("/me/password")
    public ResponseEntity<?> changePassword(
            @RequestAttribute("user") User user,
            @Valid @RequestBody ChangePasswordRequest request) {

        userService.changePassword(
                user,
                request
        );

        return ResponseEntity.ok(
                "Password changed successfully"
        );
    }
}