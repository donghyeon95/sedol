package org.example.sedol.common.error;

import java.util.HashMap;
import java.util.Map;

import org.example.sedol.common.Exception.streaming.OBSException;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

	// Validation Error Handler
	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ResponseEntity<Map<String, String>> handleValidationException(MethodArgumentNotValidException e) {

		Map<String, String> errors = new HashMap<>();
		for (FieldError error : e.getBindingResult().getFieldErrors()) {
			errors.put(error.getField(), error.getDefaultMessage());
		}

		return ResponseEntity.badRequest().body(errors);
	}

	@ExceptionHandler(OBSException.class)
	public ResponseEntity<String> handleObsException(OBSException e) {
		return ResponseEntity.badRequest().body("");
	}
}
