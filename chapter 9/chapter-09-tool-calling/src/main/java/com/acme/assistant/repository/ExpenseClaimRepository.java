package com.acme.assistant.repository;

import com.acme.assistant.entity.ExpenseClaimEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ExpenseClaimRepository extends JpaRepository<ExpenseClaimEntity, String> {
}
