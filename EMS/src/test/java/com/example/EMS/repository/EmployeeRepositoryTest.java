package com.example.EMS.repository;

import com.example.EMS.model.dto.EmployeeRequestDto;
import com.example.EMS.model.entity.Employee;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
public class EmployeeRepositoryTest {

    @Autowired
    EmployeeRepository employeeRepository;

    @Autowired
    TestEntityManager testEntityManager;

    @Test
    public void testSaveEmployee_SaveSuccessWithGeneratedId(){

        //Prepare input request
        Employee employee = new Employee();
        employee.setName("Sachin");
        employee.setSalary(888888);
        employee.setDepartment("Cricket");
        employee.setEmail("sachin@gmail.com");

        //Perform the request
        Employee employeeSaved = testEntityManager.persistFlushFind(employee);

        assertThat(employeeSaved).isNotNull();
        assertThat(employeeSaved.getId()).isNotNull();
        assertThat(employeeSaved.getName()).isEqualTo("Sachin");
        assertThat(employeeSaved.getSalary()).isEqualTo(888888);

    }

    @Test
    public void testExistsByEmail_False(){

        //Prepare input
        EmployeeRequestDto employeeRequestDto = new EmployeeRequestDto(
                "Sachin",
                888888,
                "Cricket",
                "sachin@gmail.com"
        );

        //Perform
        boolean exists = employeeRepository.existsByEmail(employeeRequestDto.getEmail());

        assertThat(exists).isFalse();
    }

    @Test
    public void testExistsByEmail_True(){

        //Prepare input and save
        Employee employee = new Employee();
        employee.setName("Virat");
        employee.setSalary(555555);
        employee.setDepartment("Cricket");
        employee.setEmail("virat@gmail.com");

        testEntityManager.persistAndFlush(employee);

        //Perform
        boolean exists = employeeRepository.existsByEmail("virat@gmail.com");

        assertThat(exists).isTrue();
    }




}
