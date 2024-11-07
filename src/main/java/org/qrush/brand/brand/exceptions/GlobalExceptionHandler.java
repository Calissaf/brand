package org.qrush.brand.brand.exceptions;

import org.hibernate.service.spi.ServiceException;
import org.springframework.http.*;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.net.URI;
import java.util.stream.Collectors;

@ControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(BrandNotFoundException.class)
    public ProblemDetail handleBrandNotFoundException(BrandNotFoundException ex, WebRequest request) {
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, ex.getMessage());
        problemDetail.setInstance(URI.create(request.getContextPath()));
        return problemDetail;
    }

    @ExceptionHandler(BrandAlreadyExists.class)
    public ProblemDetail handleBrandAlreadyExists(BrandAlreadyExists ex, WebRequest request) {
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.CONFLICT, ex.getMessage());
        problemDetail.setInstance(URI.create(request.getContextPath()));
        return problemDetail;
    }

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex, HttpHeaders headers, HttpStatusCode statusCode, WebRequest request) {
        var validationErrors = ex.getBindingResult().getFieldErrors().stream().collect(Collectors.toMap(FieldError::getField, FieldError::getDefaultMessage));

        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, "Invalid request parameters");
        problemDetail.setInstance(URI.create(request.getContextPath()));
        problemDetail.setProperty("invalid-params", validationErrors);

        return new ResponseEntity<>(problemDetail, headers, statusCode);
    }

    @ExceptionHandler(ServiceException.class)
    public ResponseEntity<ProblemDetail> handleServiceException(ServiceException ex, WebRequest request) {
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.INTERNAL_SERVER_ERROR, ex.getMessage());
        problemDetail.setInstance(URI.create(request.getContextPath()));
        return new ResponseEntity<>(problemDetail, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
