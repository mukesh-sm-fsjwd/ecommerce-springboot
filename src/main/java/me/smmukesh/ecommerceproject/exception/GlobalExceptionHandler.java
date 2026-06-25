package me.smmukesh.ecommerceproject.exception;

import me.smmukesh.ecommerceproject.dto.response.APIResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String,String>> myMethodArgumentNotValidException(MethodArgumentNotValidException e){
        Map<String,String> errors = new HashMap<>();
        e.getBindingResult().getAllErrors().forEach(err ->{
            String fieldName = ((FieldError)err).getField();
            String reason = err.getDefaultMessage();
            errors.put(fieldName,reason);
        });
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(errors);
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<APIResponse> myResourceNotFoundException(ResourceNotFoundException e){
        String message = e.getMessage();

        APIResponse apiResponse = new APIResponse();
        apiResponse.setMessage(message);
        apiResponse.setStatus(false);

        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(apiResponse);
    }

    @ExceptionHandler(APIException.class)
    public ResponseEntity<APIResponse> myAPIException(APIException e){
        String message = e.getMessage();

        APIResponse apiResponse = new APIResponse();
        apiResponse.setMessage(message);
        apiResponse.setStatus(false);

        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(apiResponse);
    }

}
