package com.project.ecommerce.security;

import com.project.ecommerce.entity.User;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

@Component
public class SecurityUtil {

    public boolean isAdmin(Authentication authentication) {

        return authentication != null
                && authentication.getAuthorities()
                .stream()
                .anyMatch(authority ->
                        authority.getAuthority().equals("ROLE_ADMIN"));
    }

    public boolean isOwner(
            Authentication authentication,
            Long userId) {

        if (authentication == null) {
            return false;
        }

        Object principal = authentication.getPrincipal();

        if (!(principal instanceof User user)) {
            return false;
        }

        return user.getId().equals(userId);
    }
}