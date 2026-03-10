package com.example.EMS.service;

import com.example.EMS.exceptions.DuplicateEmailException;
import com.example.EMS.model.dto.EmployeeRequestDto;
import com.example.EMS.model.dto.EmployeeResponseDto;
import com.example.EMS.model.entity.Employee;
import com.example.EMS.repository.EmployeeRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;


import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@SpringBootTest
public class EmployeeServiceTest {

    @Autowired
    EmployeeService employeeService;

    @MockitoBean
    EmployeeRepository employeeRepository;

    @MockitoBean
    ModelMapper modelMapper;

    private EmployeeRequestDto employeeRequestDto;
    private EmployeeResponseDto employeeResponseDto;
    private Employee employee;
    private Employee savedEmployee;

    @BeforeEach
    void setUp(){

        //Prepare request Dto
        employeeRequestDto = new EmployeeRequestDto(
                "Sachin",
                888888,
                "Cricket",
                "sachin@gmail.com"
        );

        employee = new Employee(
                10L,
                "Sachin",
                888888,
                "Cricket",
                "sachin@gmail.com"
        );

        //Prepare response Dto
        employeeResponseDto = new EmployeeResponseDto(
                10L,
                "Sachin",
                888888,
                "Cricket",
                "sachin@gmail.com"
        );

    }

    @Test
    void testAddEmployee_Success(){

        //Mock service inner method calls
        when(employeeRepository.existsByEmail(employeeRequestDto.getEmail())).thenReturn(false);
        when(employeeRepository.save(any(Employee.class))).thenReturn(employee);
        when(modelMapper.map(employeeRequestDto, Employee.class)).thenReturn(employee);
        when(modelMapper.map(employee, EmployeeResponseDto.class)).thenReturn(employeeResponseDto);

        //Perform request
        EmployeeResponseDto result = employeeService.addEmployee(employeeRequestDto);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(10);
        assertThat(result.getName()).isEqualTo("Sachin");
        assertThat(result.getSalary()).isEqualTo(888888);
        assertThat(result.getDepartment()).isEqualTo("Cricket");
        assertThat(result.getEmail()).isEqualTo("sachin@gmail.com");

        //Verify the interactions
        verify(employeeRepository).existsByEmail(employeeRequestDto.getEmail());
        verify(employeeRepository).save(employee);
        verify(modelMapper).map(employeeRequestDto, Employee.class);
        verify(modelMapper).map(employee, EmployeeResponseDto.class);

    }

    @Test
    void testAddEmployee_ServiceThrowsException(){

        //Mock service inner method calls to throw business exception
        when(employeeRepository.existsByEmail(employeeRequestDto.getEmail())).thenReturn(true);

        //Perform request
        assertThatThrownBy(()->employeeService.addEmployee(employeeRequestDto))

                .isInstanceOf(DuplicateEmailException.class)
                .hasMessageContaining("Email already exists");

        //Verify the interactions
        verify(employeeRepository).existsByEmail(employeeRequestDto.getEmail());
        verify(employeeRepository, never()).save(employee);

    }

}
