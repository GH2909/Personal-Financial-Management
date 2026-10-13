package Personal.Finance.Manager.service;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

import Personal.Finance.Manager.dto.request.LoginRequest;
import Personal.Finance.Manager.dto.request.RegisterRequest;
import Personal.Finance.Manager.dto.response.AuthResponse;
import Personal.Finance.Manager.dto.response.UserResponse;
import Personal.Finance.Manager.model.User;
import Personal.Finance.Manager.repository.UserRepository;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    @Transactional
    public AuthResponse register(RegisterRequest request) {

        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email already exists");
        }

        User user = new User();

        user.setFullname(request.getFullname());
        user.setEmail(request.getEmail());

        user.setPassword(
                passwordEncoder.encode(request.getPassword())
        );

        User savedUser = userRepository.save(user);

        String token = jwtService.generateToken(
                savedUser.getEmail()
        );

        UserResponse userResponse = new UserResponse(
                savedUser.getUserId(),
                savedUser.getFullname(),
                savedUser.getEmail()
        );

        return new AuthResponse(
                token,
                userResponse
        );
    }

    public AuthResponse login(LoginRequest request) {

        User user = userRepository
                .findByEmail(request.getEmail())
                .orElseThrow(() ->
                        new RuntimeException("Invalid email or password")
                );

        boolean passwordMatches =
                passwordEncoder.matches(
                        request.getPassword(),
                        user.getPassword()
                );

        if (!passwordMatches) {
            throw new RuntimeException(
                    "Invalid email or password"
            );
        }

        String token = jwtService.generateToken(
                user.getEmail()
        );

        UserResponse userResponse = new UserResponse(
                user.getUserId(),
                user.getFullname(),
                user.getEmail()
        );

        return new AuthResponse(
                token,
                userResponse
        );
    }
}