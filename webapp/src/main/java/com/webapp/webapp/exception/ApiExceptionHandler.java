package com.webapp.webapp.exception;

import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@Order(Ordered.HIGHEST_PRECEDENCE)
@ControllerAdvice
public class ApiExceptionHandler extends ResponseEntityExceptionHandler {


    @ExceptionHandler(ResourceNotFoundException.class)
    protected ResponseEntity<Object> handleResourceNotFound(
            ResourceNotFoundException ex) {
        ApiError apiError = new ApiError("Entity Not Found", HttpStatus.NOT_FOUND, null);
        apiError.setMessage(ex.getMessage());
        return buildResponseEntity(apiError);
    }

    @ExceptionHandler(BadRequestException.class)
    protected ResponseEntity<Object> handleBadRequestException(
            BadRequestException ex) {
        ApiError apiError = new ApiError("Bad Request Exception", HttpStatus.BAD_REQUEST, null);
        apiError.setMessage(ex.getMessage());
        apiError.setField(ex.getField());
        return buildResponseEntity(apiError);
    }

    @ExceptionHandler(UnauthorizedError.class)
    protected ResponseEntity<Object> handleAuthorizeError(UnauthorizedError ex) {
        ApiError apiError = new ApiError("Bad Request Exception", HttpStatus.UNAUTHORIZED, null);
        apiError.setMessage(ex.getMessage());
        apiError.setField("password");
        return buildResponseEntity(apiError);
    }


    private ResponseEntity<Object> buildResponseEntity(ApiError apiError) {
        return new ResponseEntity<>(apiError, apiError.getStatus());
    }

    public static class ApiError {
        private String message;
        private HttpStatus status;
        private String field;


        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }

        public HttpStatus getStatus() {
            return status;
        }

        public void setStatus(HttpStatus status) {
            this.status = status;
        }

        public void setField(String field) {
            this.field = field;
        }

        public ApiError(String message, HttpStatus status, String field) {
            this.message = message;
            this.status = status;
            this.field = field;
        }
    }
}
