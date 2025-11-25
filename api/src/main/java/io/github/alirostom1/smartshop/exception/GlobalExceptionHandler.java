package io.github.alirostom1.smartshop.exception;

import io.github.alirostom1.smartshop.dto.response.common.ApiResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.util.List;

@ControllerAdvice
public class GlobalExceptionHandler{

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Void>> handleValidationExceptions(
            MethodArgumentNotValidException ex, HttpServletRequest request){
        List<String> errors = ex.getBindingResult().getFieldErrors()
                .stream()
                .map(err -> err.getField() + ": " + err.getDefaultMessage())
                .toList();
        ApiResponse<Void> apiResponse = ApiResponse.<Void>builder()
                .success(false)
                .message("Invalid request body!")
                .path(request.getRequestURI())
                .status(400)
                .errors(errors)
                .build();
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(apiResponse);
    }
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ApiResponse<Void>> handleConstraintViolationException(
            ConstraintViolationException ex,HttpServletRequest request){
        List<String> errors = ex.getConstraintViolations()
                .stream()
                .map(violation -> extractNameFromViolation(violation) + ": " + violation.getMessage())
                .toList();
        ApiResponse<Void> apiResponse = ApiResponse.<Void>builder()
                .success(false)
                .message("Invalid url parameters!")
                .path(request.getRequestURI())
                .errors(errors)
                .status(400)
                .build();
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(apiResponse);
    }
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ApiResponse<Void>> handleTypeMismatch(
            MethodArgumentTypeMismatchException ex, HttpServletRequest request) {

        String message = String.format("Parameter '%s' must be %s",
                ex.getName(), ex.getRequiredType().getSimpleName());

        ApiResponse<Void> apiResponse = ApiResponse.<Void>builder()
                .success(false)
                .status(400)
                .message(message)
                .path(request.getRequestURI())
                .build();
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(apiResponse);
    }
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<ApiResponse<Void>> handleMethodNotSupported(
        HttpRequestMethodNotSupportedException ex,HttpServletRequest request
    ){
        String message = String.format("Method %s not supported for this endpoint",ex.getMethod());
        ApiResponse<Void> apiResponse = ApiResponse.<Void>builder()
                .success(false)
                .path(request.getRequestURI())
                .message(message)
                .status(405)
                .build();
        return ResponseEntity.status(HttpStatus.METHOD_NOT_ALLOWED).body(apiResponse);
    }
    @ExceptionHandler(HttpMediaTypeNotSupportedException.class)
    public ResponseEntity<ApiResponse<Void>> handleMediaTypeNotSupported(
            HttpMediaTypeNotSupportedException ex,HttpServletRequest request
    ){
        String message = String.format("Content type %s not supported",ex.getContentType());
        ApiResponse<Void> apiResponse = ApiResponse.<Void>builder()
                .success(false)
                .path(request.getRequestURI())
                .message(message)
                .status(415)
                .build();
        return ResponseEntity.status(HttpStatus.UNSUPPORTED_MEDIA_TYPE).body(apiResponse);
    }


    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Void>> handleGenericExceptions(
            Exception ex,HttpServletRequest request){
        ApiResponse<Void> apiResponse = ApiResponse.<Void>builder()
                .success(false)
                .message("internal server error, please try again later!")
                .status(500)
                .path(request.getRequestURI())
                .build();
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(apiResponse);
    }


    private String extractNameFromViolation(ConstraintViolation<?> violation){
        String path = violation.getPropertyPath().toString();
        return path.contains(".") ? path.substring(path.lastIndexOf(".") + 1) : path;
    }
}
