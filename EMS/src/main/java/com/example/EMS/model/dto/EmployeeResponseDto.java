package com.example.EMS.model.dto;

public class EmployeeResponseDto {
    private long id;
    private String name;
    private int salary;
    private String department;
    private String email;

    public EmployeeResponseDto(){

    }

    public EmployeeResponseDto(long id, String name, int salary, String department, String email) {
        this.id = id;
        this.name = name;
        this.salary = salary;
        this.department = department;
        this.email = email;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
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
        return "EmployeeResponseDto{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", salary=" + salary +
                ", department='" + department + '\'' +
                ", email='" + email + '\'' +
                '}';
    }
}
