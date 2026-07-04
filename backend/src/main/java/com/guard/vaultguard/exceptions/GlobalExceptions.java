package com.guard.vaultguard.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptions {

    @ExceptionHandler(value=IllegalTransactionException.class)
    public ResponseEntity<Map<String,Object>> handleIllegalTransactionException(IllegalTransactionException ex){
        Map<String,Object> map =
                buildErrorResponse(HttpStatus.BAD_REQUEST, ex, "Transactions has Invalid Details");

        return new ResponseEntity<>(map, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(value = DuplicateUsernameException.class)
    public ResponseEntity<Map<String,Object>> handleDuplicateUsernameException(DuplicateUsernameException ex) {
        Map<String, Object> map =
                buildErrorResponse(HttpStatus.CONFLICT, ex, "Username Already Exists");

        return new ResponseEntity<>(map, HttpStatus.CONFLICT);
    }

    @ExceptionHandler(value = InvalidUserDataException.class)
    public ResponseEntity<Map<String, Object>> handleInvalidUserDataException(InvalidUserDataException ex) {
        Map<String, Object> map =
                buildErrorResponse(HttpStatus.BAD_REQUEST, ex, "Invalid User Data");

        return new ResponseEntity<>(map, HttpStatus.BAD_REQUEST);
    }

    private Map<String, Object> buildErrorResponse(HttpStatus status, Exception ex, String issue) {
        Map<String, Object> map = new HashMap<>();
        map.put("message", ex.getMessage());
        map.put("Status", status);
        map.put("issue", issue);
        map.put("Timestamp", LocalDateTime.now().toString());

        return map;
    }
}
