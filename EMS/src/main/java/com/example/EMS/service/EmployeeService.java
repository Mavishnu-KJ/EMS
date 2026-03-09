package com.example.EMS.service;

import com.example.EMS.model.dto.EmployeeRequestDto;
import com.example.EMS.model.dto.EmployeeResponseDto;

public interface EmployeeService {

    EmployeeResponseDto addEmployee(EmployeeRequestDto employeeRequestDto);

}
