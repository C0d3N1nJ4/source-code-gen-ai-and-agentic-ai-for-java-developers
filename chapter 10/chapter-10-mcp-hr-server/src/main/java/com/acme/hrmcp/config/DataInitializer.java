package com.acme.hrmcp.config;

import com.acme.hrmcp.entity.EmployeeEntity;
import com.acme.hrmcp.entity.HrPolicyEntity;
import com.acme.hrmcp.repository.EmployeeRepository;
import com.acme.hrmcp.repository.HrPolicyRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DataInitializer {

    @Bean
    CommandLineRunner seedData(EmployeeRepository employeeRepository, HrPolicyRepository hrPolicyRepository) {
        return args -> {
            if (employeeRepository.count() == 0) {
                EmployeeEntity e1 = new EmployeeEntity();
                e1.setEmployeeId("E1001");
                e1.setFullName("Avery Stone");
                e1.setEmail("avery.stone@acme.com");
                e1.setDepartment("Engineering");
                e1.setAnnualLeaveEntitlement(25);
                e1.setAnnualLeaveUsed(7);

                EmployeeEntity e2 = new EmployeeEntity();
                e2.setEmployeeId("E2001");
                e2.setFullName("Jordan Lee");
                e2.setEmail("jordan.lee@acme.com");
                e2.setDepartment("Finance");
                e2.setAnnualLeaveEntitlement(25);
                e2.setAnnualLeaveUsed(12);

                employeeRepository.save(e1);
                employeeRepository.save(e2);
            }

            if (hrPolicyRepository.count() == 0) {
                HrPolicyEntity p1 = new HrPolicyEntity();
                p1.setTitle("Annual Leave Policy");
                p1.setContent("Employees are entitled to annual leave based on grade and tenure. Leave must be approved by the direct manager at least 2 business days before start date.");

                HrPolicyEntity p2 = new HrPolicyEntity();
                p2.setTitle("Leave Submission SLA");
                p2.setContent("Leave requests are reviewed by managers within 2 business days. HR escalates pending approvals beyond SLA.");

                hrPolicyRepository.save(p1);
                hrPolicyRepository.save(p2);
            }
        };
    }
}
