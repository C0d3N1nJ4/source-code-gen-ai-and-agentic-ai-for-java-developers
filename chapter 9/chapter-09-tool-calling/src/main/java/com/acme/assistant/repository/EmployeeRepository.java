package com.acme.assistant.repository;

import com.acme.assistant.entity.EmployeeEntity;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EmployeeRepository extends JpaRepository<EmployeeEntity, String> {

    Optional<EmployeeEntity> findByFullNameIgnoreCase(String fullName);
}
