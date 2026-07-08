package com.acme.assistant.service;

import com.acme.assistant.entity.EmployeeEntity;
import com.acme.assistant.model.ExpenseClaim;
import com.acme.assistant.repository.ExpenseClaimRepository;
import java.math.BigDecimal;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class ExpenseServiceTest {

    private final EmployeeDirectoryService employeeDirectoryService = mock(EmployeeDirectoryService.class);
    private final ExpenseClaimRepository expenseClaimRepository = mock(ExpenseClaimRepository.class);
    private final ExpenseService expenseService = new ExpenseService(employeeDirectoryService, expenseClaimRepository);

    @Test
    void shouldSubmitExpenseClaim() {
        when(employeeDirectoryService.requireEmployee("EMP-1001"))
                .thenReturn(new EmployeeEntity("EMP-1001", "Sarah Connor", "sarah.connor@acme.com", "EMP-2001", 14));

        ExpenseClaim request = new ExpenseClaim(
                null,
                "EMP-1001",
                "HOTEL",
                new BigDecimal("189.50"),
                "USD",
                "Hotel stay for client workshop",
                null);

        var result = expenseService.submitExpense(request);

        assertThat(result.claimId()).startsWith("EXP-");
        assertThat(result.status()).isEqualTo("SUBMITTED");
        assertThat(result.amount()).isEqualByComparingTo("189.50");
        verify(expenseClaimRepository).save(any());
    }
}
