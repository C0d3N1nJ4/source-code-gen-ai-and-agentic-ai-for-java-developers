package com.acme.hrmcp.service;

import com.acme.hrmcp.model.HrPolicyRecord;
import com.acme.hrmcp.repository.HrPolicyRepository;
import jakarta.validation.constraints.NotBlank;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.util.List;

@Validated
@Service
public class HrPolicyService {

    private final HrPolicyRepository hrPolicyRepository;

    public HrPolicyService(HrPolicyRepository hrPolicyRepository) {
        this.hrPolicyRepository = hrPolicyRepository;
    }

    public List<HrPolicyRecord> searchHrPolicies(@NotBlank String query) {
        return hrPolicyRepository
                .findTop5ByTitleContainingIgnoreCaseOrContentContainingIgnoreCase(query, query)
                .stream()
                .map(policy -> {
                    String excerpt = policy.getContent().length() > 180
                            ? policy.getContent().substring(0, 180) + "..."
                            : policy.getContent();
                    return new HrPolicyRecord(policy.getId(), policy.getTitle(), excerpt);
                })
                .toList();
    }
}
