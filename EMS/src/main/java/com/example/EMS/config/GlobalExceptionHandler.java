package com.example.EMS.config;

import com.example.EMS.exceptions.DuplicateEmailException;
import com.example.EMS.exceptions.ResourceNotFoundException;
import com.example.EMS.model.dto.ErrorResponse;
import jakarta.validation.ConstraintViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.HandlerMethodValidationException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(DuplicateEmailException.class)
    ResponseEntity<ErrorResponse> handleDuplicateEmailException(DuplicateEmailException e){

        ErrorResponse errorResponse = new ErrorResponse(
                HttpStatus.CONFLICT.value(),
                "Duplicate Email Id",
                List.of(e.getMessage()),
                LocalDateTime.now()
        );

        return new ResponseEntity<>(errorResponse, HttpStatus.CONFLICT);

    }

    //NOTE : MethodArgumentNotValidException (for @Valid @RequestBody)
    //Eg. /addEmployee, name must not be blank, salary must be greater than 0
    @ExceptionHandler(MethodArgumentNotValidException.class)
    ResponseEntity<ErrorResponse> handleMethodArgumentNotValidException(MethodArgumentNotValidException e){

        List<String> errorList = e.getBindingResult().getFieldErrors().stream()
                .map(err -> err.getField() +" : "+err.getDefaultMessage())
                .toList();

        ErrorResponse errorResponse = new ErrorResponse(
                HttpStatus.BAD_REQUEST.value(),
                "Validation Failed",
                errorList,
                LocalDateTime.now()
        );

        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    //handler for list of objects (eg. /addEmployees) - sometimes the exception might be BindException
    @ExceptionHandler(BindException.class)
    ResponseEntity<ErrorResponse> handleBindException(BindException e){
        List<String> errorList = e.getBindingResult().getFieldErrors().stream()
                .map(err -> err.getField() + " : " +err.getDefaultMessage())
                .toList();

        ErrorResponse errorResponse = new ErrorResponse(
                HttpStatus.BAD_REQUEST.value(),
                "Binding Validation Failed",
                errorList,
                LocalDateTime.now()
        );

        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    //NOTE : If validation fails → MethodArgumentNotValidException (for @Valid @RequestBody)
    // or ConstraintViolationException (for @Valid @PathVariable/@RequestParam)
    @ExceptionHandler(ConstraintViolationException.class)
    ResponseEntity<ErrorResponse> handleConstraintViolationException(ConstraintViolationException e){

        List<String> errorList = e.getConstraintViolations().stream()
                .map(err -> err.getPropertyPath() + " : " +err.getMessage())
                .toList();

        ErrorResponse errorResponse = new ErrorResponse(
                HttpStatus.BAD_REQUEST.value(),
                "Constraint Validation failed",
                errorList,
                LocalDateTime.now()
        );

        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);

    }

    //eg. GetMapping("/api/employees/{id}", /api/employees/abc)
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    ResponseEntity<ErrorResponse> handleMethodArgumentTypeMismatchException(MethodArgumentTypeMismatchException e){

        List<String> errorList = List.of("Invalid parameter : "+e.getName()+" Expected type "+e.getRequiredType().getSimpleName() +" but, got "+e.getValue());
        ErrorResponse errorResponse = new ErrorResponse(
                HttpStatus.BAD_REQUEST.value(),
                "Type mismatch",
                errorList,
                LocalDateTime.now()
        );

        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);

    }

    @ExceptionHandler(ResourceNotFoundException.class)
    ResponseEntity<ErrorResponse> handleResourceNotFoundException(ResourceNotFoundException e){
        List<String> errorList = List.of(e.getMessage());

        ErrorResponse errorResponse = new ErrorResponse(
                HttpStatus.NOT_FOUND.value(),
                "Resource Not Found",
                errorList,
                LocalDateTime.now()
        );

        return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
    }

    //eg. @Positive(message = "minSalary must be greater than 0") @RequestParam (name="minSalary", required = false) int salary falls in global error
    @ExceptionHandler(HandlerMethodValidationException.class)
    ResponseEntity<ErrorResponse> handleHandlerMethodValidationException(HandlerMethodValidationException e){

        List<String> errorList = new ArrayList<>();

        e.getAllErrors().forEach(err->{
            if(err instanceof FieldError fieldError){
                errorList.add(fieldError.getField() + " : " +fieldError.getDefaultMessage());
            }else{
                //Rare global error
                errorList.add("global : "+err.getDefaultMessage());
            }
        });

        ErrorResponse errorResponse = new ErrorResponse(
                HttpStatus.BAD_REQUEST.value(),
                "Handler Method Validation Failed",
                errorList,
                LocalDateTime.now()
        );

        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }


}
