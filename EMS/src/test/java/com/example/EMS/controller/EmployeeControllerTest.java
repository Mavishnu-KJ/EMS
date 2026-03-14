package com.example.EMS.controller;

import com.example.EMS.exceptions.DuplicateEmailException;
import com.example.EMS.exceptions.ResourceNotFoundException;
import com.example.EMS.model.dto.EmployeeRequestDto;
import com.example.EMS.model.dto.EmployeeResponseDto;
import com.example.EMS.service.EmployeeService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.util.List;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest
public class EmployeeControllerTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @MockitoBean
    EmployeeService employeeService;

    @Test
    public void testAddEmployee_Success() throws Exception{

        //Prepare request dto
        EmployeeRequestDto employeeRequestDto = new EmployeeRequestDto(
                "Sachin",
                888888,
                "Cricket",
                "sachin@gmail.com"
        );

        //Prepare expected response dto
        EmployeeResponseDto employeeResponseDto = new EmployeeResponseDto(
                10L,
                "Sachin",
                888888,
                "Cricket",
                "sachin@gmail.com"
        );

        //Mock service behavior
        Mockito.when(employeeService.addEmployee(any(EmployeeRequestDto.class))).thenReturn(employeeResponseDto);

        //Perform POST request
        mockMvc.perform(post("/api/employees/addEmployee")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(employeeRequestDto)))
                .andExpect(status().isCreated())
                //.andExpect(header().string("location", "http://localhos//api/employees/10"))
                .andExpect(header().string("location", containsString("/api/employees/10")))
                .andExpect(jsonPath("$.id").value("10"))
                .andExpect(jsonPath("$.name").value("Sachin"));

        //Verify service was called only once
        verify(employeeService, times(1)).addEmployee(any(EmployeeRequestDto.class));

    }

    @Test
    public void testAddEmployee_ValidationFailure() throws Exception{

        //prepare invalid request dto
        EmployeeRequestDto employeeRequestDto = new EmployeeRequestDto(
                "",
                888888,
                "Cricket",
                "sachin@gmail.com"
        );

        //Perform POST request
        mockMvc.perform(post("/api/employees/addEmployee")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(employeeRequestDto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.shortSummaryMessage").value("Validation Failed"))
                .andExpect(jsonPath("$.errorList").isArray())
                .andExpect(jsonPath("$.errorList").isNotEmpty())
                .andExpect(jsonPath("$.errorList[0]").value(containsString("must not be blank")));


        //Verify the service was never called
        verify(employeeService, never()).addEmployee(employeeRequestDto);

    }

    @Test
    public void testAddEmployee_ServiceThrowsException() throws Exception{

        //Prepare request Dto
        EmployeeRequestDto employeeRequestDto = new EmployeeRequestDto(
                "Sachin",
                888888,
                "Cricket",
                "sachin@gmail.com"
        );

        //Mock service behavior
        when(employeeService.addEmployee(any(EmployeeRequestDto.class))).thenThrow(new DuplicateEmailException());

        //Perform POST request
        mockMvc.perform(post("/api/employees/addEmployee")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(employeeRequestDto)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.shortSummaryMessage").value(containsString("Duplicate Email")))
                .andExpect(jsonPath("$.errorList").isArray())
                .andExpect(jsonPath("$.errorList").isNotEmpty())
                .andExpect(jsonPath("$.errorList[0]").value(containsString("already exists")));

        //Verify the service was called once
        verify(employeeService, times(1)).addEmployee(any(EmployeeRequestDto.class));

    }

    @Test
    public void addEmployees_Success() throws Exception{

        //Prepare request Dto
        List<EmployeeRequestDto> employeeRequestDtoList = List.of(
                new EmployeeRequestDto("Sachin", 888888, "Cricket", "sachin@gmail.com"),
                new EmployeeRequestDto("Virat", 555555, "Cricket", "virat@gmail.com")
        );

        //Prepare response Dto
        List<EmployeeResponseDto> employeeResponseDtoList = List.of(
                new EmployeeResponseDto(10L, "Sachin", 888888, "Cricket", "sachin@gmail.com"),
                new EmployeeResponseDto(18L, "Virat", 555555, "Cricket", "virat@gmail.com")
        );

        //Mock service behavior
        //when(employeeService.addEmployees(employeeRequestDtoList)).thenReturn(employeeResponseDtoList); //Did not work
        when(employeeService.addEmployees(anyList())).thenReturn(employeeResponseDtoList);

        //Perform POST request
        mockMvc.perform(post("/api/employees/addEmployees")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(employeeRequestDtoList)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$").isNotEmpty())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].name").value("Sachin"))
                .andExpect(jsonPath("$[1].name").value("Virat"));

        //Verify the service was called once
        verify(employeeService, times(1)).addEmployees(anyList());

    }

    @Test
    public void testAddEmployees_ServiceThrowsException() throws Exception{

        //Prepare request dto
        List<EmployeeRequestDto> employeeRequestDtoList = List.of();

        //Mock service behavior
        when(employeeService.addEmployees(anyList())).thenThrow(new ResourceNotFoundException("Input request is empty"));

        //Perform POST request
        mockMvc.perform(post("/api/employees/addEmployees")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(employeeRequestDtoList)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("shortSummaryMessage").value("Resource Not Found"))
                .andExpect(jsonPath("$.errorList").isArray())
                .andExpect(jsonPath("$.errorList").isNotEmpty())
                .andExpect(jsonPath("$.errorList[0]").value(containsString("is empty")));

        //verify the service was called once
        verify(employeeService, times(1)).addEmployees(anyList());

    }

    @Test
    public void testGetEmployeeById_Success() throws Exception{
        //Prepare request
        Long id = 10L;

        //Prepare expected response dto
        EmployeeResponseDto employeeResponseDto = new EmployeeResponseDto(
                10L,
                "Sachin",
                888888,
                "Cricket",
                "sachin@gmail.com"
        );

        //Mock service behavior
        when(employeeService.getEmployeeById(anyLong())).thenReturn(employeeResponseDto);

        //Perform GET request
        mockMvc.perform(get("/api/employees/{id}", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(10))
                .andExpect(jsonPath("$.name").value("Sachin"));

        //Verify the service was called once
        verify(employeeService, times(1)).getEmployeeById(id);

    }

    @Test
    public void testGetEmployeeById_ValidationFailure() throws Exception {

        //Prepare invalid input
        String id = "abc";

        //Perform GET request
        mockMvc.perform(get("/api/employees/{id}", id))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("shortSummaryMessage").value("Type mismatch"))
                .andExpect(jsonPath("$.errorList").isArray())
                .andExpect(jsonPath("$.errorList").isNotEmpty())
                .andExpect(jsonPath("$.errorList[0]").value(containsString("Invalid parameter")));

        //Verify the service was never called
        verify(employeeService, never()).getEmployeeById(anyLong());

    }

    @Test
    public void testGetEmployeeById_ServiceThrowsException() throws Exception{

        //Prepare request
        Long id = 10L;

        //Mock service behavior
        when(employeeService.getEmployeeById(anyLong())).thenThrow(new ResourceNotFoundException("Resource not found for : "+id));

        //Perform GET request
        mockMvc.perform(get("/api/employees/{id}", id))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.shortSummaryMessage").value("Resource Not Found"))
                .andExpect(jsonPath("$.errorList").isArray())
                .andExpect(jsonPath("$.errorList").isNotEmpty())
                .andExpect(jsonPath("$.errorList[0]").value(containsString("Resource not found")));

        //Verify the service was called once
        verify(employeeService, times(1)).getEmployeeById(anyLong());

    }

    @Test
    public void testSearchEmployeeById_Success() throws Exception{
        //Prepare request
        Long id = 10L;

        //Prepare expected response dto
        EmployeeResponseDto employeeResponseDto = new EmployeeResponseDto(
                10L,
                "Sachin",
                888888,
                "Cricket",
                "sachin@gmail.com"
        );

        //Mock service behavior
        when(employeeService.searchEmployeeById(anyLong())).thenReturn(employeeResponseDto);

        //Perform GET request
        mockMvc.perform(get("/api/employees/searchEmployeeById").queryParam("id", String.valueOf(id)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(10))
                .andExpect(jsonPath("$.name").value("Sachin"));

        //Verify the service was called once
        verify(employeeService, times(1)).searchEmployeeById(anyLong());

    }

    @Test
    public void testSearchEmployeesWithPagination1_Success() throws Exception{
        //Prepare request Dto
        Pageable pageable = PageRequest.of(0, 5);
        MultiValueMap<String, String> queryParamMultiValueMap = new LinkedMultiValueMap<>();
        queryParamMultiValueMap.add("name", "Sachin");
        queryParamMultiValueMap.add("page", "0");
        queryParamMultiValueMap.add("size", "5");

        //Prepare expected ResponseDto
        List<EmployeeResponseDto> employeeResponseDtoList = List.of(
                new EmployeeResponseDto(1L, "Sachin Tendulkar", 888888, "Cricket", "sachinTendulkar@gmail.com"),
                new EmployeeResponseDto(2L, "Sachin", 888888, "Cricket", "sachin@gmail.com"),
                new EmployeeResponseDto(3L, "Sachin", 999999, "Cricket", "sachin1@gmail.com")
        );

        Page<EmployeeResponseDto> employeeResponseDtoPage = new PageImpl<>(employeeResponseDtoList, pageable, 3);

        //Mock service behavior
        when(employeeService.searchEmployeesWithPagination1(
                any(), any(), any(), any(), any(Pageable.class)))
                .thenReturn(employeeResponseDtoPage);

        //Perform GET request
        mockMvc.perform(get("/api/employees/searchEmployeesWithPagination1")
                        .queryParams(queryParamMultiValueMap)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content").isNotEmpty())
                .andExpect(jsonPath("$.content[0].name").value("Sachin Tendulkar"))
                .andExpect(jsonPath("$.content[1].name").value("Sachin"))
                .andExpect(jsonPath("$.totalElements").value(3))
                .andExpect(jsonPath("$.totalPages").value(1))
                .andExpect(jsonPath("$.number").value(0))
                .andExpect(jsonPath("$.size").value(5));


        //Verify the service was called once
        verify(employeeService, times(1)).searchEmployeesWithPagination1(
                anyString(), any(), any(), any(), any(Pageable.class)
        );
    }

    @Test
    void testSearchEmployeesWithPagination1_ValidationFailure() throws Exception {
        // If you have @Min validation on salary in DTO, Spring will throw MethodArgumentNotValidException or HandlerMethodValidationException
        mockMvc.perform(get("/api/employees/searchEmployeesWithPagination1")
                        .param("minSalary", "-5000")
                        .param("name", "Sachin"))
                .andExpect(status().isBadRequest());
    }



}
