package com.upc.campusflow.Controller;

import com.upc.campusflow.DTO.ErrorRespuesta; // Asegúrate de que esta clase existe y es correcta
import com.upc.campusflow.Exception.AccesoDenegadoException;
import com.upc.campusflow.Exception.DuplicateEntryException; // ¡Importa tu excepción personalizada!
import com.upc.campusflow.Exception.RecursoNoEncontradoException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    // --- Manejo de RecursoNoEncontradoException (404 Not Found) ---
    @ExceptionHandler(RecursoNoEncontradoException.class)
    public ResponseEntity<ErrorRespuesta> manejarRecursoNoEncontrado(RecursoNoEncontradoException ex) {
        ErrorRespuesta error = new ErrorRespuesta(
                ex.getMessage(),
                LocalDateTime.now().format(FORMATTER),
                HttpStatus.NOT_FOUND.value()
        );
        return new ResponseEntity<>(error, HttpStatus.NOT_FOUND);
    }

    // --- Manejo de DuplicateEntryException (409 Conflict) ---
    @ExceptionHandler(DuplicateEntryException.class)
    public ResponseEntity<ErrorRespuesta> manejarDuplicado(DuplicateEntryException ex) {
        ErrorRespuesta error = new ErrorRespuesta(
                ex.getMessage(), // Tu mensaje personalizado del servicio
                LocalDateTime.now().format(FORMATTER),
                HttpStatus.CONFLICT.value() // Código 409 Conflict es apropiado para duplicados
        );
        return new ResponseEntity<>(error, HttpStatus.CONFLICT);
    }

    // --- Manejo de AccesoDenegadoException (403 Forbidden) ---
    @ExceptionHandler(AccesoDenegadoException.class)
    public ResponseEntity<ErrorRespuesta> manejarAccesoDenegado(AccesoDenegadoException ex) {
        ErrorRespuesta error = new ErrorRespuesta(
                ex.getMessage(),
                LocalDateTime.now().format(FORMATTER),
                HttpStatus.FORBIDDEN.value()
        );
        return new ResponseEntity<>(error, HttpStatus.FORBIDDEN);
    }

    // --- Manejo de validaciones de DTOs (@Valid, 400 Bad Request) ---
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> manejarArgumentosInvalidos(MethodArgumentNotValidException ex) {
        Map<String, String> errores = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errores.put(fieldName, errorMessage);
        });
        return new ResponseEntity<>(errores, HttpStatus.BAD_REQUEST);
    }

    // --- Manejo de ModelMapper.ConfigurationException ---
    @ExceptionHandler(org.modelmapper.ConfigurationException.class)
    public ResponseEntity<ErrorRespuesta> manejarModelMapperConfigurationException(org.modelmapper.ConfigurationException ex) {
        ErrorRespuesta error = new ErrorRespuesta(
                "Error de configuración en el mapeo de datos: " + ex.getMessage(),
                LocalDateTime.now().format(FORMATTER),
                HttpStatus.INTERNAL_SERVER_ERROR.value()
        );
        return new ResponseEntity<>(error, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    // --- Manejo de HibernateException: "identifier of an instance was altered" ---
    @ExceptionHandler(org.hibernate.HibernateException.class)
    public ResponseEntity<ErrorRespuesta> manejarHibernateException(org.hibernate.HibernateException ex) {
        ErrorRespuesta error = new ErrorRespuesta(
                "Error al intentar modificar un identificador de entidad: " + ex.getMessage(),
                LocalDateTime.now().format(FORMATTER),
                HttpStatus.BAD_REQUEST.value()
        );
        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }

    // --- Manejo de cualquier otra excepción no esperada (500 Internal Server Error) ---
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorRespuesta> manejarOtrasExcepciones(Exception ex) {
        ex.printStackTrace(); // MUY IMPORTANTE: Loguea la excepción completa aquí
        ErrorRespuesta error = new ErrorRespuesta(
                "Ocurrió un error inesperado en el sistema. Por favor, contacta a soporte.",
                LocalDateTime.now().format(FORMATTER),
                HttpStatus.INTERNAL_SERVER_ERROR.value()
        );
        return new ResponseEntity<>(error, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}