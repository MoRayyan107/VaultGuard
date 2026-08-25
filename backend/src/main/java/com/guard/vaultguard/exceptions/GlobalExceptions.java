package com.guard.vaultguard.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptions {

    // when transaction details are invalid
    @ExceptionHandler(value=IllegalTransactionException.class)
    public ResponseEntity<Map<String,Object>> handleIllegalTransactionException(IllegalTransactionException ex){
        Map<String,Object> map =
                buildErrorResponse(HttpStatus.BAD_REQUEST, ex, "Transactions has Invalid Details");

        return new ResponseEntity<>(map, HttpStatus.BAD_REQUEST);
    }

    // Wrong credentials provided by user
    @ExceptionHandler(value = InvalidCredentialException.class)
    public ResponseEntity<Map<String,Object>> handleInvalidCredentialException(InvalidCredentialException ex){
        Map<String, Object> map =
                buildErrorResponse(HttpStatus.UNAUTHORIZED, ex, "Invalid Credentials");

        return new ResponseEntity<>(map, HttpStatus.UNAUTHORIZED);
    }

    // When user registers but that same user already exists in the DB
    @ExceptionHandler(value = DuplicateUsernameException.class)
    public ResponseEntity<Map<String,Object>> handleDuplicateUsernameException(DuplicateUsernameException ex) {
        Map<String, Object> map =
                buildErrorResponse(HttpStatus.CONFLICT, ex, "Username Already Exists");

        return new ResponseEntity<>(map, HttpStatus.CONFLICT);
    }

    // When registering user provides invalid data (like empty username, password etc.)
    @ExceptionHandler(value = InvalidUserDataException.class)
    public ResponseEntity<Map<String, Object>> handleInvalidUserDataException(InvalidUserDataException ex) {
        Map<String, Object> map =
                buildErrorResponse(HttpStatus.BAD_REQUEST, ex, "Invalid User Data");

        return new ResponseEntity<>(map, HttpStatus.BAD_REQUEST);
    }

    // when user tries to access a resource that does not exist in the DB
    @ExceptionHandler(value = BankCodeNotFoundException.class)
    public ResponseEntity<Map<String, Object>> handleBankCodeNotFoundException(BankCodeNotFoundException ex) {
        Map<String, Object> map =
                buildErrorResponse(HttpStatus.NOT_FOUND, ex, "Bank not found");

        return new ResponseEntity<>(map, HttpStatus.NOT_FOUND);
    }

    // when user tries to process a transaction that already exists in the DB (Idempotency check)
    @ExceptionHandler(value = DuplicateTransactionException.class)
    public ResponseEntity<Map<String,Object>> handleDuplicateTransactionException(DuplicateTransactionException ex){
        Map<String, Object> map =
                buildErrorResponse(HttpStatus.CONFLICT, ex, "Duplicate Transaction");

        return new ResponseEntity<>(map, HttpStatus.CONFLICT);
    }

    // Provided exception by the spring convention, handels wrong data types provided in the request body
    @ExceptionHandler(value= MethodArgumentTypeMismatchException.class)
    public ResponseEntity<Map<String, Object>> handleMethodArgumentTypeMismatchException(MethodArgumentTypeMismatchException ex) {
        Map<String, Object> map =
                buildErrorResponse(HttpStatus.BAD_REQUEST, ex, "Invalid Argument Type");

        return new ResponseEntity<>(map, HttpStatus.BAD_REQUEST);
    }

    // says it all, an exception for illegal risk level provided in the request body
    @ExceptionHandler(value = IllegalRiskLevelException.class)
    public ResponseEntity<Map<String, Object>> handleIllegalRiskLevelException(IllegalRiskLevelException ex) {
        Map<String, Object> map =
                buildErrorResponse(HttpStatus.BAD_REQUEST, ex, "Invalid Risk Level");

        return new ResponseEntity<>(map, HttpStatus.BAD_REQUEST);
    }

    // Same, just for illegal transaction status provided in the request body
    @ExceptionHandler(value=IllegalTransactionStatusException.class)
    public ResponseEntity<Map<String, Object>> handleIllegalTransactionStatusException(IllegalTransactionStatusException ex) {
        Map<String, Object> map =
                buildErrorResponse(HttpStatus.BAD_REQUEST, ex, "Invalid Transaction Status");

        return new ResponseEntity<>(map, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(value=IllegalTransactionTypeException.class)
    public ResponseEntity<Map<String, Object>> handleIllegalTransactionTypeException(IllegalTransactionTypeException ex) {
        Map<String, Object> map =
                buildErrorResponse(HttpStatus.BAD_REQUEST, ex, "Invalid Transaction Type");

        return new ResponseEntity<>(map, HttpStatus.BAD_REQUEST);
    }


    // The method to build the error response map with the required fields
    private Map<String, Object> buildErrorResponse(HttpStatus status, Exception ex, String issue) {
        Map<String, Object> map = new HashMap<>();
        map.put("message", ex.getMessage());
        map.put("Status", status.value());
        map.put("issue", issue);
        map.put("Timestamp", LocalDateTime.now().toString());

        return map;
    }
}
