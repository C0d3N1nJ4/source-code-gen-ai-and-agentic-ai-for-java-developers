package com.acme.hrmcp.repository;

import com.acme.hrmcp.entity.HrPolicyEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface HrPolicyRepository extends JpaRepository<HrPolicyEntity, Long> {

    List<HrPolicyEntity> findTop5ByTitleContainingIgnoreCaseOrContentContainingIgnoreCase(String title, String content);
}
