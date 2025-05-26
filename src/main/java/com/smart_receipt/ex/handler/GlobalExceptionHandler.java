package com.smart_receipt.ex.handler;


import com.smart_receipt.dto.ErrorDto;
import com.smart_receipt.ex.AiResponseException;
import com.smart_receipt.ex.CategoryAlreadyExistException;
import com.smart_receipt.ex.CategoryNotFoundException;
import com.smart_receipt.ex.EmailNotFoundException;
import com.smart_receipt.ex.ProductNotFoundException;
import com.smart_receipt.ex.ReceiptNotFoundException;
import com.smart_receipt.ex.UserAlreadyExistException;
import com.smart_receipt.ex.UserForbiddenException;
import com.smart_receipt.ex.UserNotFoundException;
import com.smart_receipt.ex.UserUnauthorizedException;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ResponseStatus(value = HttpStatus.NOT_FOUND)
    @ExceptionHandler({UserNotFoundException.class, EmailNotFoundException.class, CategoryNotFoundException.class,
            ReceiptNotFoundException.class, ProductNotFoundException.class})
    public ResponseEntity<ErrorDto> handleNotFoundException(Exception ex) {

        log.error("not found ex: {}", ex.getMessage());
        return new ResponseEntity<>(ErrorDto.builder()
                .message(ex.getMessage())
                .statusCode(HttpStatus.NOT_FOUND.value())
                .build(), HttpStatus.NOT_FOUND);
    }

    @ResponseStatus(value = HttpStatus.CONFLICT)
    @ExceptionHandler({UserAlreadyExistException.class, CategoryAlreadyExistException.class})
    public ResponseEntity<ErrorDto> handleConflictException(RuntimeException ex) {

        log.error("conflict: {}", ex.getMessage());
        return new ResponseEntity<>(ErrorDto.builder()
                .message(ex.getMessage())
                .statusCode(HttpStatus.CONFLICT.value())
                .build(), HttpStatus.CONFLICT);
    }

    @ResponseStatus(value = HttpStatus.UNAUTHORIZED)
    @ExceptionHandler(UserUnauthorizedException.class)
    public ResponseEntity<ErrorDto> handleNoAuthorityException(UserUnauthorizedException ex) {

        log.error("unauthorized: {}", ex.getMessage());
        return new ResponseEntity<>(ErrorDto.builder()
                .message("unauthorized")
                .statusCode(HttpStatus.UNAUTHORIZED.value())
                .build(), HttpStatus.UNAUTHORIZED);
    }

    @ResponseStatus(value = HttpStatus.FORBIDDEN)
    @ExceptionHandler(UserForbiddenException.class)
    public ResponseEntity<ErrorDto> handleForbiddenException(UserForbiddenException ex) {

        log.error("forbidden: {}", ex.getMessage());
        return new ResponseEntity<>(ErrorDto.builder()
                .message("forbidden")
                .statusCode(HttpStatus.FORBIDDEN.value())
                .build(), HttpStatus.FORBIDDEN);
    }

    @ResponseStatus(value = HttpStatus.BAD_REQUEST)
    @ExceptionHandler({
            ConstraintViolationException.class, MethodArgumentNotValidException.class, IllegalArgumentException.class,
            HttpMessageNotReadableException.class, DataIntegrityViolationException.class, MissingServletRequestParameterException.class
    })
    public ResponseEntity<ErrorDto> handleWrongArgsException(Exception ex) {

        log.error("wrong args: {}", ex.getMessage());
        return new ResponseEntity<>(ErrorDto.builder()
                .message("bad request")
                .statusCode(HttpStatus.BAD_REQUEST.value())
                .build(), HttpStatus.BAD_REQUEST);
    }

    @ResponseStatus(value = HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(AiResponseException.class)
    public ResponseEntity<ErrorDto> handleAiResponseException(AiResponseException ex) {

        log.error("ai response problem: {}", ex.getMessage());
        return new ResponseEntity<>(ErrorDto.builder()
                .message("ai response problem")
                .statusCode(HttpStatus.INTERNAL_SERVER_ERROR.value())
                .build(), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ResponseStatus(value = HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ErrorDto> handleOtherException(Exception ex) {

        log.error("some problem: {}", ex.toString());
        return new ResponseEntity<>(ErrorDto.builder()
                .message(ex.getMessage())
                .statusCode(HttpStatus.INTERNAL_SERVER_ERROR.value())
                .build(), HttpStatus.INTERNAL_SERVER_ERROR);
    }
}