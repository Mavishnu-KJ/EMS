package com.example.EMS.controller;

import com.example.EMS.model.dto.EmployeeRequestDto;
import com.example.EMS.model.dto.EmployeeResponseDto;
import com.example.EMS.service.EmployeeService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.hamcrest.core.StringContains;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.head;
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




}
