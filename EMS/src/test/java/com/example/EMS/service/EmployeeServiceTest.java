package com.example.EMS.service;

import com.example.EMS.exceptions.DuplicateEmailException;
import com.example.EMS.exceptions.ResourceNotFoundException;
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


import java.util.List;
import java.util.Optional;

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

    @Test
    void testAddEmployees_Success(){

        //Prepare request Dto list
        List<EmployeeRequestDto> employeeRequestDtoList = List.of(
                employeeRequestDto
        );
        List<Employee> employeeList = List.of(employee);

        //Mock service inner method calls
        when(employeeRepository.existsByEmail(employeeRequestDto.getEmail())).thenReturn(false);
        when(employeeRepository.saveAll(employeeList)).thenReturn(employeeList);
        when(modelMapper.map(employeeRequestDto, Employee.class)).thenReturn(employee);
        when(modelMapper.map(employee, EmployeeResponseDto.class)).thenReturn(employeeResponseDto);

        //Perform
        List<EmployeeResponseDto> employeeResponseDtoList = employeeService.addEmployees(employeeRequestDtoList);

        assertThat(employeeResponseDtoList).isNotNull();
        assertThat(employeeResponseDtoList.getFirst().getName()).isEqualTo("Sachin");

        //Verify the interactions
        verify(employeeRepository).existsByEmail(employeeRequestDto.getEmail());
        verify(employeeRepository).saveAll(employeeList);
        verify(modelMapper).map(employeeRequestDto, Employee.class);
        verify(modelMapper).map(employee, EmployeeResponseDto.class);

    }

    @Test
    void testAddEmployees_ServiceThrowsException(){

        //Prepare request Dto list
        List<EmployeeRequestDto> employeeRequestDtoList = List.of();

        assertThatThrownBy(()->employeeService.addEmployees(employeeRequestDtoList))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("empty");

        //Verify the interactions
        verify(employeeRepository, never()).existsByEmail(employeeRequestDto.getEmail());
        verify(employeeRepository, never()).save(employee);

    }

    @Test //DuplicateEmailException
    void testAddEmployees_ServiceThrowsException1(){
        //Prepare request Dto list
        List<EmployeeRequestDto> employeeRequestDtoList = List.of(
                employeeRequestDto
        );

        List<Employee> employeeList = List.of(employee);

        //Mock service inner method calls
        when(employeeRepository.existsByEmail(employeeRequestDto.getEmail())).thenReturn(true);

        //Perform
        assertThatThrownBy(()->employeeService.addEmployees(employeeRequestDtoList))
                .isInstanceOf(DuplicateEmailException.class)
                .hasMessageContaining("already exists");

        //Verify the interactions
        verify(employeeRepository).existsByEmail(employeeRequestDto.getEmail());
        verify(employeeRepository, never()).saveAll(employeeList);
    }

    @Test //DuplicateEmailException in the request itself
    void testAddEmployees_ServiceThrowsException2(){
        //Prepare request Dto list
        List<EmployeeRequestDto> employeeRequestDtoList = List.of(
                employeeRequestDto,
                employeeRequestDto
        );

        List<Employee> employeeList = List.of(employee);

        //Mock service inner method calls
        when(employeeRepository.existsByEmail(employeeRequestDto.getEmail())).thenReturn(false);

        //Perform
        assertThatThrownBy(()->employeeService.addEmployees(employeeRequestDtoList))
                .isInstanceOf(DuplicateEmailException.class)
                .hasMessageContaining("Duplicate email in the request itself");

        //Verify the interactions
        verify(employeeRepository).existsByEmail(employeeRequestDto.getEmail());
        verify(employeeRepository, never()).saveAll(employeeList);
    }

    @Test
    void testGetEmployeeById_Success(){
        //Prepare request
        Long id = 10L;

        //Mock service inner method calls
        when(employeeRepository.findById(anyLong())).thenReturn(Optional.ofNullable(employee));
        when(modelMapper.map(employee, EmployeeResponseDto.class)).thenReturn(employeeResponseDto);

        //Perform
        EmployeeResponseDto result = employeeService.getEmployeeById(anyLong());

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(10);
        assertThat(result.getName()).isEqualTo("Sachin");

        //Verify the interactions
        verify(employeeRepository).findById(anyLong());
        verify(modelMapper).map(employee, EmployeeResponseDto.class);

    }

    @Test
    void testGetEmployeeById_ServiceThrowsException(){
        //Prepare request
        Long id = 10L;

        //Mock service inner method calls
        when(employeeRepository.findById(anyLong())).thenThrow(new ResourceNotFoundException("Resource not found for : "+id));

        //Perform
        assertThatThrownBy(()->employeeService.getEmployeeById(anyLong()))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Resource not found");

        //Verify the interactions
        verify(employeeRepository).findById(anyLong());
        verify(modelMapper, never()).map(employee, EmployeeResponseDto.class);

    }


}
