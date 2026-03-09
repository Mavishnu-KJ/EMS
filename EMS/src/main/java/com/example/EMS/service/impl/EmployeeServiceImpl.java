package com.example.EMS.service.impl;

import com.example.EMS.exceptions.DuplicateEmailException;
import com.example.EMS.model.dto.EmployeeRequestDto;
import com.example.EMS.model.dto.EmployeeResponseDto;
import com.example.EMS.model.entity.Employee;
import com.example.EMS.repository.EmployeeRepository;
import com.example.EMS.service.EmployeeService;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class EmployeeServiceImpl implements EmployeeService {

    private static final Logger logger = LoggerFactory.getLogger(EmployeeServiceImpl.class);

    private EmployeeRepository employeeRepository;
    private ModelMapper modelMapper;

    public EmployeeServiceImpl(EmployeeRepository employeeRepository, ModelMapper modelMapper){
        this.employeeRepository = employeeRepository;
        this.modelMapper = modelMapper;
    }

    @Override
    public EmployeeResponseDto addEmployee(EmployeeRequestDto employeeRequestDto){
        logger.info("addEmployee, employeeRequestDto is {}", employeeRequestDto);

        //Throw DuplicateEmailException if validation fails
        if(employeeRequestDto != null && !employeeRequestDto.getEmail().isBlank() && employeeRepository.existsByEmail(employeeRequestDto.getEmail())){
            throw new DuplicateEmailException(employeeRequestDto.getEmail());
        }

        //Map EmployeeRequestDto to Employee
        Employee employee = modelMapper.map(employeeRequestDto, Employee.class);
        logger.info("addEmployee, employee is {}", employee);

        Employee employeeSaved = employeeRepository.save(employee);
        logger.info("addEmployee, employeeSaved is {}", employeeSaved);

        //Map Employee to EmployeeResponseDto
        EmployeeResponseDto employeeResponseDto = modelMapper.map(employeeSaved, EmployeeResponseDto.class);
        logger.info("addEmployee, employeeResponseDto is {}", employeeResponseDto);

        return employeeResponseDto;

    }

}
