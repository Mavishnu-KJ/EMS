package com.example.EMS.service.impl;

import com.example.EMS.exceptions.DuplicateEmailException;
import com.example.EMS.model.dto.EmployeeRequestDto;
import com.example.EMS.model.dto.EmployeeResponseDto;
import com.example.EMS.model.entity.Employee;
import com.example.EMS.repository.EmployeeRepository;
import com.example.EMS.service.EmployeeService;
import jakarta.transaction.Transactional;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

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
    @Transactional
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

    @Override
    @Transactional
    public List<EmployeeResponseDto> addEmployees(List<EmployeeRequestDto> employeeRequestDtoList){
        logger.info("addEmployees, employeeRequestDtoList is {}", employeeRequestDtoList);

        //Map employee request dto to employee
        List<Employee> employeeList = new ArrayList<>();
        ArrayList<String> duplicateEmailList =new ArrayList<>();
        //System.out.println("duplicateEmailList is Empty ? "+duplicateEmailList.isEmpty()); //true

        for(EmployeeRequestDto employeeRequestDto : employeeRequestDtoList){

            if(employeeRequestDto != null && !employeeRequestDto.getEmail().isBlank()){
                //Validate email
                if(employeeRepository.existsByEmail(employeeRequestDto.getEmail())){
                    duplicateEmailList.add(employeeRequestDto.getEmail());
                }else if(duplicateEmailList.isEmpty()){ //Add only if there is no even one duplicate email
                    employeeList.add(modelMapper.map(employeeRequestDto, Employee.class));
                }
            }
        }
        logger.info("addEmployees, employeeList is {}", employeeList);

        //Throw duplicate email exception if the existing email list is not empty
        if (!duplicateEmailList.isEmpty()) {
            logger.info("addEmployees, duplicateEmailList is {}", duplicateEmailList);
            throw new DuplicateEmailException(duplicateEmailList.toString());
        }

        //Save
        List<Employee> savedEmployeeList = employeeRepository.saveAll(employeeList);
        logger.info("addEmployees, savedEmployeeList is {}", savedEmployeeList);

        //Map employee to employee response dto
        List<EmployeeResponseDto> employeeResponseDtoList = new ArrayList<>();

        for(Employee emp : savedEmployeeList){
            employeeResponseDtoList.add(modelMapper.map(emp, EmployeeResponseDto.class));
        }
        logger.info("addEmployees, employeeResponseDtoList is {}", employeeResponseDtoList);

        return employeeResponseDtoList;

    }


}
