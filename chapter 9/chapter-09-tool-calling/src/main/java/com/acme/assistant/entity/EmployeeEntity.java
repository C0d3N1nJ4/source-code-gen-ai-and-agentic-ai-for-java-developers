package com.acme.assistant.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "employees")
public class EmployeeEntity {

    @Id
    private String id;

    @Column(nullable = false)
    private String fullName;

    @Column(nullable = false)
    private String email;

    private String managerId;

    @Column(nullable = false)
    private int remainingLeaveDays;

    public EmployeeEntity() {
    }

    public EmployeeEntity(String id, String fullName, String email, String managerId, int remainingLeaveDays) {
        this.id = id;
        this.fullName = fullName;
        this.email = email;
        this.managerId = managerId;
        this.remainingLeaveDays = remainingLeaveDays;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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

    public String getManagerId() {
        return managerId;
    }

    public void setManagerId(String managerId) {
        this.managerId = managerId;
    }

    public int getRemainingLeaveDays() {
        return remainingLeaveDays;
    }

    public void setRemainingLeaveDays(int remainingLeaveDays) {
        this.remainingLeaveDays = remainingLeaveDays;
    }
}
