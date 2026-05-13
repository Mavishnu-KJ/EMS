package com.example.EMS.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.CONFLICT, reason = "Duplicate Email Exception")
public class DuplicateEmailException extends RuntimeException{

    public DuplicateEmailException(){
        super("Email already exists in DB");
    }

    public DuplicateEmailException(String email){
        super("Email already exists in DB: "+email);
    }

    public DuplicateEmailException(String customMessage, String email){
        super(customMessage+ " : " +email);
    }

    public DuplicateEmailException(String cause, Throwable throwable){
        super(cause, throwable);
    }

}
