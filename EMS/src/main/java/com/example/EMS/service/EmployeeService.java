package com.example.EMS.service;

import com.example.EMS.model.dto.EmployeeRequestDto;
import com.example.EMS.model.dto.EmployeeResponseDto;

import java.util.List;

public interface EmployeeService {

    EmployeeResponseDto addEmployee(EmployeeRequestDto employeeRequestDto);
    List<EmployeeResponseDto> addEmployees(List<EmployeeRequestDto> employeeRequestDtoList);

    EmployeeResponseDto getEmployeeById(Long id);
    List<EmployeeResponseDto> getAllEmployees();

    EmployeeResponseDto searchEmployeeById(Long id);
    List<EmployeeResponseDto> searchEmployees(String name, Integer salary, String department, String email);

    EmployeeResponseDto updateEmployeeById(EmployeeRequestDto employeeRequestDto, Long id);
    List<EmployeeResponseDto> updateEmployeeByName(EmployeeRequestDto employeeRequestDto, String name);

}
