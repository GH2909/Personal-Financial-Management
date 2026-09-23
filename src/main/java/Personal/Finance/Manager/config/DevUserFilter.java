package Personal.Finance.Manager.config;

import java.io.IOException;

import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import Personal.Finance.Manager.model.User;
import Personal.Finance.Manager.repository.UserRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class DevUserFilter extends OncePerRequestFilter {

    private final UserRepository userRepository;

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain)
            throws ServletException, IOException {

        String userIdHeader = request.getHeader("X-Test-User-Id");

        if (userIdHeader != null && !userIdHeader.isBlank()) {
            try {
                Long userId = Long.parseLong(userIdHeader);

                User user = userRepository.findById(userId)
                        .orElse(null);

                if (user != null) {
                    request.setAttribute("user", user);
                }

            } catch (NumberFormatException ignored) {
                // Invalid user ID -> no user attached
            }
        }

        filterChain.doFilter(request, response);
    }
}