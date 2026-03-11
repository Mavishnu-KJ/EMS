package com.example.EMS.controller;


import com.example.EMS.model.dto.EmployeeRequestDto;
import com.example.EMS.model.dto.EmployeeResponseDto;
import com.example.EMS.service.EmployeeService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/employees")
public class EmployeeController {

    private static final Logger logger = LoggerFactory.getLogger(EmployeeController.class);

    private EmployeeService employeeService;

    public EmployeeController(EmployeeService employeeService){
        this.employeeService = employeeService;
    }

    @PostMapping("/addEmployee")
    ResponseEntity<EmployeeResponseDto> addEmployee(@Valid @RequestBody EmployeeRequestDto employeeRequestDto){
        logger.info("addEmployee, employeeRequestDto is {}", employeeRequestDto);

        EmployeeResponseDto employeeAdded = employeeService.addEmployee(employeeRequestDto);
        logger.info("addEmployee, employeeAdded is {}", employeeAdded);

        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                        .replacePath("/api/employees/{id}")
                        .buildAndExpand(employeeAdded.getId())
                        .toUri();
        logger.info("addEmployee, location is {}", location);

        return ResponseEntity.created(location).body(employeeAdded);
    }

    @PostMapping("/addEmployees")
    ResponseEntity<List<EmployeeResponseDto>> addEmployees(@Valid @RequestBody List< @Valid EmployeeRequestDto> employeeRequestDtoList){
        logger.info("addEmployees, employeeRequestDtoList is {}", employeeRequestDtoList);

        List<EmployeeResponseDto> employeeResponseDtoList = employeeService.addEmployees(employeeRequestDtoList);
        logger.info("addEmployees, employeeResponseDtoList is {}", employeeResponseDtoList);

        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .build()
                .toUri();

        return ResponseEntity.created(location).body(employeeResponseDtoList);
    }

    @GetMapping("{id}") //http://localhost:8080/api/employees/12
    ResponseEntity<EmployeeResponseDto> getEmployeeById(@PathVariable("id") Long id){
        logger.info("getEmployeeById, id is {}", id);

        EmployeeResponseDto employeeResponseDto = employeeService.getEmployeeById(id);
        logger.info("getEmployeeById, employeeResponseDto is {}", employeeResponseDto);

        return ResponseEntity.ok(employeeResponseDto);

    }

    @GetMapping
    ResponseEntity<List<EmployeeResponseDto>> getAllEmployees(){
        logger.info("getAllEmployees");

        List<EmployeeResponseDto> employeeResponseDtoList = employeeService.getAllEmployees();
        logger.info("getAllEmployees, employeeResponseDtoList is {}", employeeResponseDtoList);

        return ResponseEntity.ok(employeeResponseDtoList);
    }

    @GetMapping("searchEmployeeById") //http://localhost:8080/api/employees/searchEmployeeById?id={id}
    ResponseEntity<EmployeeResponseDto> searchEmployeeById(@RequestParam("id") Long id){
        logger.info("searchEmployeeById, id is {}", id);

        EmployeeResponseDto employeeResponseDto = employeeService.searchEmployeeById(id);
        logger.info("searchEmployeeById, employeeResponseDto is {}", employeeResponseDto);

        return ResponseEntity.ok(employeeResponseDto);

    }


}
