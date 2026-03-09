package com.example.EMS.model.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;

public class EmployeeRequestDto {

    @NotBlank
    private String name;
    @Positive(message = "salary must be greater than 0")
    private int salary;
    private String department;
    @Email
    private String email;

    public EmployeeRequestDto(){

    }

    public EmployeeRequestDto(String name, int salary, String department, String email) {
        this.name = name;
        this.salary = salary;
        this.department = department;
        this.email = email;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getSalary() {
        return salary;
    }

    public void setSalary(int salary) {
        this.salary = salary;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @Override
    public String toString() {
        return "EmployeeRequestDto{" +
                "name='" + name + '\'' +
                ", salary=" + salary +
                ", department='" + department + '\'' +
                ", email='" + email + '\'' +
                '}';
    }
}
