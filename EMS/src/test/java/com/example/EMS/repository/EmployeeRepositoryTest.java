package com.example.EMS.repository;

import com.example.EMS.model.dto.EmployeeRequestDto;
import com.example.EMS.model.dto.EmployeeResponseDto;
import com.example.EMS.model.entity.Employee;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.util.List;
import java.util.Optional;

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

    @Test
    public void testFindById_Success(){
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

        //Use either repository save or testEntityManager flush
        Employee savedEmployee1 = testEntityManager.persistFlushFind(employee1);
        Employee savedEmployee2 = testEntityManager.persistFlushFind(employee2);

        //Perform
        Optional<Employee> foundEmployee1 = employeeRepository.findById(savedEmployee1.getId());
        Optional<Employee> foundEmployee2 = employeeRepository.findById(savedEmployee2.getId());

        assertThat(foundEmployee1).isPresent();
        assertThat(foundEmployee1.get().getName()).isEqualTo("Virat");
        assertThat(foundEmployee1.get().getId()).isEqualTo(savedEmployee1.getId()); // verify ID match

        assertThat(foundEmployee2).isPresent();
        assertThat(foundEmployee2.get().getName()).isEqualTo("Rohit");
        assertThat(foundEmployee2.get().getId()).isEqualTo(savedEmployee2.getId()); // verify ID match

    }

    @Test
    void testFindAll_WithNameFilter_ShouldReturnMatchingEmployees() {
        // Given - insert test data
        testEntityManager.persist(new Employee(null, "Sachin Tendulkar", 900000, "Cricket", "sachin@test.com"));
        testEntityManager.persist(new Employee(null, "Virat Kohli", 555555, "Cricket", "virat@test.com"));
        testEntityManager.persist(new Employee(null, "Rohit Sharma", 800000, "Cricket", "rohit@test.com"));
        testEntityManager.flush();

        // Specification for name containing "Tendulkar" (case-insensitive)
        Specification<Employee> spec = (root, query, cb) ->
                cb.like(cb.lower(root.get("name")), "%tendulkar%");

        Pageable pageable = PageRequest.of(0, 10);

        // When
        Page<Employee> result = employeeRepository.findAll(spec, pageable);

        // Then
        assertThat(result.getTotalElements()).isEqualTo(1);
        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).getName()).isEqualTo("Sachin Tendulkar");
        assertThat(result.getContent().get(0).getEmail()).isEqualTo("sachin@test.com");
    }




}
