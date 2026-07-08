package com.acme.assistant.tool;

import com.acme.assistant.model.ExpenseClaim;
import com.acme.assistant.service.ExpenseService;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.stereotype.Service;

@Service
public class ExpenseTools {

    private final ExpenseService expenseService;

    public ExpenseTools(ExpenseService expenseService) {
        this.expenseService = expenseService;
    }

    @Tool(description = "Submit an employee expense claim with amount, category, and description.")
    public ExpenseClaim submitExpense(ExpenseClaim claim) {
        return expenseService.submitExpense(claim);
    }
}
