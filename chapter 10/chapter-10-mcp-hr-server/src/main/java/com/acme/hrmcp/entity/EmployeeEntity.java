package com.acme.hrmcp.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "employees")
public class EmployeeEntity {

    @Id
    @Column(name = "employee_id", nullable = false, length = 32)
    private String employeeId;

    @Column(nullable = false)
    private String fullName;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String department;

    @Column(nullable = false)
    private int annualLeaveEntitlement;

    @Column(nullable = false)
    private int annualLeaveUsed;

    public String getEmployeeId() {
        return employeeId;
    }

    public void setEmployeeId(String employeeId) {
        this.employeeId = employeeId;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public int getAnnualLeaveEntitlement() {
        return annualLeaveEntitlement;
    }

    public void setAnnualLeaveEntitlement(int annualLeaveEntitlement) {
        this.annualLeaveEntitlement = annualLeaveEntitlement;
    }

    public int getAnnualLeaveUsed() {
        return annualLeaveUsed;
    }

    public void setAnnualLeaveUsed(int annualLeaveUsed) {
        this.annualLeaveUsed = annualLeaveUsed;
    }
}
