package com.example.EMS.service;

import com.example.EMS.model.dto.EmployeeRequestDto;
import com.example.EMS.model.dto.EmployeeResponseDto;

import java.util.List;

public interface EmployeeService {

    EmployeeResponseDto addEmployee(EmployeeRequestDto employeeRequestDto);
    List<EmployeeResponseDto> addEmployees(List<EmployeeRequestDto> employeeRequestDtoList);

}
