package com.example.EMS.repository;

import com.example.EMS.model.dto.EmployeeRequestDto;
import com.example.EMS.model.entity.Employee;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.util.List;

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

    @Test
    public void testSaveAllEmployees_SaveAllSuccessWithGeneratedIds(){

        //Prepare input and save all
        Employee employee1 = new Employee();
        employee1.setName("Virat");
        employee1.setSalary(555555);
        employee1.setDepartment("Cricket");
        employee1.setEmail("virat@gmail.com");

        Employee employee2 = new Employee();
        employee2.setName("Rohit");
        employee2.setSalary(555555);
        employee2.setDepartment("Cricket");
        employee2.setEmail("rohit@gmail.com");

        List<Employee> employeeList = List.of(employee1, employee2);

        List<Employee> savedEmployeeList = employeeRepository.saveAll(employeeList);

        assertThat(savedEmployeeList).isNotNull();
        assertThat(savedEmployeeList.getFirst().getName()).isEqualTo("Virat");
        for(Employee emp : savedEmployeeList){
           assertThat(emp).isNotNull();
           assertThat(emp.getId()).isNotNull();
        }

    }




}
