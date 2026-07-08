package com.acme.assistant.chapter12.services;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

import com.acme.assistant.chapter12.exceptions.ForbiddenException;
import com.acme.assistant.chapter12.security.AuthorizationService;
import com.acme.assistant.chapter12.tools.ToolSecurityService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ToolSecurityServiceTest {

    @Mock
    private AuthorizationService authorizationService;

    @Test
    void shouldRejectApproveLeaveForEmployee() {
        ToolSecurityService service = new ToolSecurityService(authorizationService);
        when(authorizationService.hasRole("HR")).thenReturn(false);
        when(authorizationService.hasRole("ADMIN")).thenReturn(false);

        assertThatThrownBy(() -> service.executeBusinessAction("HR", "approve leave for EMP-1001"))
                .isInstanceOf(ForbiddenException.class);
    }
}
