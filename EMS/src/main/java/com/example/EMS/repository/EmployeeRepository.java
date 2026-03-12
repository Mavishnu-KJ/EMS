package com.example.EMS.repository;


import com.example.EMS.model.entity.Employee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EmployeeRepository extends JpaRepository<Employee, Long> {

    //Employee save(Employee employee);
    boolean existsByEmail(String email);
    List<Employee> findByName(String name);
}
