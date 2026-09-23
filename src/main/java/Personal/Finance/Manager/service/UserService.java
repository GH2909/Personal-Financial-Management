package Personal.Finance.Manager.service;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import Personal.Finance.Manager.dto.request.ChangePasswordRequest;
import Personal.Finance.Manager.dto.request.UpdateUserRequest;
import Personal.Finance.Manager.dto.response.UserResponse;
import Personal.Finance.Manager.model.User;
import Personal.Finance.Manager.repository.UserRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    // =========================
    // GET CURRENT USER
    // =========================

    @Transactional(readOnly = true)
    public UserResponse getCurrentUser(User user) {

        validateUser(user);

        return mapToResponse(user);
    }

    // =========================
    // UPDATE PROFILE
    // =========================

    @Transactional
    public UserResponse updateCurrentUser(
            User user,
            UpdateUserRequest request) {

        validateUser(user);

        if (!user.getEmail().equalsIgnoreCase(request.getEmail())
                && userRepository.existsByEmail(request.getEmail())) {

            throw new RuntimeException(
                    "Email is already in use"
            );
        }

        user.setFullname(request.getFullname());
        user.setEmail(request.getEmail());

        User updatedUser = userRepository.save(user);

        return mapToResponse(updatedUser);
    }

    // =========================
    // CHANGE PASSWORD
    // =========================

    @Transactional
    public void changePassword(
            User user,
            ChangePasswordRequest request) {

        validateUser(user);

        if (!passwordEncoder.matches(
                request.getCurrentPassword(),
                user.getPassword())) {

            throw new RuntimeException(
                    "Current password is incorrect"
            );
        }

        if (passwordEncoder.matches(
                request.getNewPassword(),
                user.getPassword())) {

            throw new RuntimeException(
                    "New password must be different from current password"
            );
        }

        user.setPassword(
                passwordEncoder.encode(
                        request.getNewPassword()
                )
        );

        userRepository.save(user);
    }

    // =========================
    // VALIDATE USER
    // =========================

    private void validateUser(User user) {

        if (user == null) {
            throw new RuntimeException(
                    "User is required"
            );
        }

        if (user.getUserId() == null) {
            throw new RuntimeException(
                    "Invalid user"
            );
        }
    }

    // =========================
    // MAPPING
    // =========================

    private UserResponse mapToResponse(User user) {

        UserResponse response = new UserResponse();

        response.setUserId(user.getUserId());
        response.setFullname(user.getFullname());
        response.setEmail(user.getEmail());

        return response;
    }
}