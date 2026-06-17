package com.guard.vaultguard.exceptions;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
public class GlobalExceptions {

    @ExceptionHandler(value=IllegalTransactionException.class)
    public ResponseEntity<Map<String,Object>> handleIllegalTransactionException(IllegalTransactionException ex){
        Map<String,Object> map = new HashMap<>();
        map.put("message", ex.getMessage());
        map.put("Status", HttpStatus.BAD_REQUEST.value());
        map.put("Issue", "Transactions has Invalid Details");
        map.put("Timestamp", LocalDateTime.now().toString());

        return new ResponseEntity<>(map, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(value = DuplicateUsernameException.class)
    public ResponseEntity<Map<String,Object>> handleDuplicateUsernameException(DuplicateUsernameException ex) {
        Map<String, Object> map = new HashMap<>();
        map.put("message", ex.getMessage());
        map.put("Status", HttpStatus.CONFLICT.value());
        map.put("Issue", "Username Already Exists");
        map.put("Timestamp", LocalDateTime.now().toString());

        return new ResponseEntity<>(map, HttpStatus.CONFLICT);
    }

    @ExceptionHandler(value = InvalidUserDataException.class)
    public ResponseEntity<Map<String, Object>> handleInvalidUserDataException(InvalidUserDataException ex) {
        Map<String, Object> map = new HashMap<>();
        map.put("message", ex.getMessage());
        map.put("Status", HttpStatus.BAD_REQUEST.value());
        map.put("Issue", "Invalid User Data");
        map.put("Timestamp", LocalDateTime.now().toString());

        return new ResponseEntity<>(map, HttpStatus.BAD_REQUEST);
    }
}
