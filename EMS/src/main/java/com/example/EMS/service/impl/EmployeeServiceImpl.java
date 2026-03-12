package com.example.EMS.service.impl;

import com.example.EMS.exceptions.DuplicateEmailException;
import com.example.EMS.exceptions.ResourceNotFoundException;
import com.example.EMS.model.dto.EmployeeRequestDto;
import com.example.EMS.model.dto.EmployeeResponseDto;
import com.example.EMS.model.entity.Employee;
import com.example.EMS.repository.EmployeeRepository;
import com.example.EMS.service.EmployeeService;
import jakarta.transaction.Transactional;
import org.hibernate.sql.results.graph.embeddable.EmbeddableLoadingLogger;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.groovy.template.GroovyTemplateProperties;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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

        if(employeeRequestDtoList.isEmpty()){
            throw new ResourceNotFoundException("Input request is empty");
        }

        //Map employee request dto to employee
        List<Employee> employeeList = new ArrayList<>();
        Set<String> seenEmailSet = new HashSet<>();
        ArrayList<String> duplicateEmailListInTheRequestDto =new ArrayList<>();
        ArrayList<String> duplicateEmailList =new ArrayList<>();

        for(EmployeeRequestDto employeeRequestDto : employeeRequestDtoList){
            if(employeeRequestDto != null && !employeeRequestDto.getEmail().isBlank()){
                //Ensure there is no duplicate email
                if(!seenEmailSet.add(employeeRequestDto.getEmail())){ // seenEmailSet.add(employeeRequestDto.email() returns true after addition, else false without adding, HashSet does not allow dupllicates
                    duplicateEmailListInTheRequestDto.add(employeeRequestDto.getEmail());
                }else if(employeeRepository.existsByEmail(employeeRequestDto.getEmail())){ //Validate email with db records
                    duplicateEmailList.add(employeeRequestDto.getEmail());
                }else if(duplicateEmailList.isEmpty()){ //Add only if there is no even one duplicate email
                    employeeList.add(modelMapper.map(employeeRequestDto, Employee.class));
                }
            }
        }
        logger.info("addEmployees, employeeList is {}", employeeList);

        //Throw duplicate email exception
        if(!duplicateEmailListInTheRequestDto.isEmpty()){
            logger.info("addEmployees, duplicateEmailListInTheRequestDto is {}", duplicateEmailListInTheRequestDto);
            throw new DuplicateEmailException("Duplicate email in the request itself", duplicateEmailListInTheRequestDto.toString());
        }

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

    @Override
    public EmployeeResponseDto getEmployeeById(Long id){
        logger.info("getEmployeeById, id is {}", id);

        Employee employee = employeeRepository.findById(id)
                .orElseThrow(()->new ResourceNotFoundException("Resource not found for the given id : "+id));
        logger.info("getEmployeeById, employee is {}", employee);

        //Map employee to employee response dto
        EmployeeResponseDto employeeResponseDto = modelMapper.map(employee, EmployeeResponseDto.class);
        logger.info("getEmployeeById, employeeResponseDto is {}", employeeResponseDto);

        return employeeResponseDto;

    }

    @Override
    public List<EmployeeResponseDto> getAllEmployees(){
        logger.info("getAllEmployees");

        List<Employee> employeeList = employeeRepository.findAll();
        logger.info("getAllEmployees, employeeList is {}", employeeList);

        //Map employee to employee response dto
        List<EmployeeResponseDto> employeeResponseDtoList = employeeList.stream()
                        .map(emp -> modelMapper.map(emp, EmployeeResponseDto.class))
                        .toList();

        logger.info("getAllEmployees, employeeResponseDtoList is {}", employeeResponseDtoList);

        return employeeResponseDtoList;

    }

    @Override
    public EmployeeResponseDto searchEmployeeById(Long id){
        logger.info("searchEmployeeById, id is {}", id);

        Employee employee = employeeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Resource not found for the given id : "+id));
        logger.info("searchEmployeeById, employee is {}", employee);

        //Map employee to employee response dto
        EmployeeResponseDto employeeResponseDto = modelMapper.map(employee, EmployeeResponseDto.class);
        logger.info("searchEmployeeById, employeeResponseDto is {}", employeeResponseDto);

        return employeeResponseDto;

    }

    @Override
    public List<EmployeeResponseDto> searchEmployees(String name, Integer salary, String department, String email){
        logger.info("searchEmployees, name is {}, salary is {}, department is {}, email is {}", name, salary, department, email);

        //Create employee list stream
        Stream<Employee> employeeListStream = employeeRepository.findAll().stream();

        if(name !=null && !name.isBlank()){
            logger.info("searchEmployees, inside name !=null, name is {}", name);
            employeeListStream = employeeListStream.filter(emp -> emp.getName().toLowerCase().contains(name.trim().toLowerCase()));
        }

        //minSalary
        if(salary !=null){
            logger.info("searchEmployees, inside salary !=null, salary is {}", salary);
            employeeListStream = employeeListStream.filter(emp -> emp.getSalary() >= salary);
        }

        if(department !=null && !department.isBlank()){
            logger.info("searchEmployees, inside department !=null, department is {}", department);
            employeeListStream = employeeListStream.filter(emp -> emp.getDepartment().equalsIgnoreCase(department));
        }

        if(email !=null && !email.isBlank()){
            logger.info("searchEmployees, inside email !=null, email is {}", email);
            employeeListStream = employeeListStream.filter(emp -> emp.getEmail().equalsIgnoreCase(email));
        }

        List<Employee> employeeList = employeeListStream.collect(Collectors.toList());
        logger.info("searchEmployees, employeeList is {}", employeeList);

        //Map employee to employee response dto
        List<EmployeeResponseDto> employeeResponseDtoList = employeeList.stream()
                .filter(Objects::nonNull)
                .map(emp -> modelMapper.map(emp, EmployeeResponseDto.class))
                .toList();
        logger.info("searchEmployees, employeeResponseDtoList is {}", employeeResponseDtoList);

        return employeeResponseDtoList;
    }


}
