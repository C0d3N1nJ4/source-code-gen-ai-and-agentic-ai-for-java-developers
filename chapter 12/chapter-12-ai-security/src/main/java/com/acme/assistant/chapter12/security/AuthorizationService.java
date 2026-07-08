package com.acme.assistant.chapter12.security;

import com.acme.assistant.chapter12.entities.DocumentEntity;
import com.acme.assistant.chapter12.exceptions.ForbiddenException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
public class AuthorizationService {

    public String currentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new ForbiddenException("Authentication is required");
        }
        return authentication.getName();
    }

    public boolean hasRole(String role) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null) {
            return false;
        }
        String expected = "ROLE_" + role.toUpperCase();
        return authentication.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals(expected));
    }

    public void assertSameUserOrAdmin(String userId) {
        if (!currentUser().equalsIgnoreCase(userId) && !hasRole("ADMIN")) {
            throw new ForbiddenException("Operation not allowed for user " + currentUser());
        }
    }

    public void assertCanReadDocument(DocumentEntity document) {
        if (document.getClassification().equalsIgnoreCase("PUBLIC")) {
            return;
        }
        if (document.getClassification().equalsIgnoreCase("INTERNAL") &&
                (hasRole("EMPLOYEE") || hasRole("HR") || hasRole("FINANCE") || hasRole("OPERATIONS") || hasRole("ADMIN"))) {
            return;
        }
        if (document.getOwnerUserId() != null && document.getOwnerUserId().equalsIgnoreCase(currentUser())) {
            return;
        }
        if (hasRole("ADMIN") || hasRole("HR") || hasRole("FINANCE")) {
            return;
        }
        throw new ForbiddenException("You are not authorized to access this document");
    }
}
