package com.acme.assistant.service;

import com.acme.assistant.entity.ExpenseClaimEntity;
import com.acme.assistant.model.ExpenseClaim;
import com.acme.assistant.repository.ExpenseClaimRepository;
import java.util.Locale;
import java.util.UUID;
import org.springframework.stereotype.Service;

@Service
public class ExpenseService {

    private final EmployeeDirectoryService employeeDirectoryService;
    private final ExpenseClaimRepository expenseClaimRepository;

    public ExpenseService(EmployeeDirectoryService employeeDirectoryService, ExpenseClaimRepository expenseClaimRepository) {
        this.employeeDirectoryService = employeeDirectoryService;
        this.expenseClaimRepository = expenseClaimRepository;
    }

    public ExpenseClaim submitExpense(ExpenseClaim claim) {
        employeeDirectoryService.requireEmployee(claim.employeeId());

        String claimId = "EXP-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase(Locale.ROOT);
        ExpenseClaimEntity entity = new ExpenseClaimEntity(
            claimId,
            claim.employeeId(),
            claim.category(),
            claim.amount(),
            claim.currency(),
            claim.description(),
            "SUBMITTED");

        expenseClaimRepository.save(entity);

        return new ExpenseClaim(
                entity.getId(),
                entity.getEmployeeId(),
                entity.getCategory(),
                entity.getAmount(),
                entity.getCurrency(),
                entity.getDescription(),
                entity.getStatus());
    }
}
