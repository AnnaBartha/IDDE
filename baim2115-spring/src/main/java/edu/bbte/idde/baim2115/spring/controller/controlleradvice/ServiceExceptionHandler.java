package edu.bbte.idde.baim2115.spring.controller.controlleradvice;

import edu.bbte.idde.baim2115.spring.controller.dto.Message;
import edu.bbte.idde.baim2115.spring.exception.EntityNotFoundException;
import edu.bbte.idde.baim2115.spring.repository.RepositoryExeption;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.concurrent.ConcurrentHashMap;

@RestControllerAdvice
@Slf4j
public class ServiceExceptionHandler {
    @ExceptionHandler(RepositoryExeption.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public final Message handleRepositoryException(RepositoryExeption e) {
        log.error("Repository Exception catch");

        ConcurrentHashMap<String, String> response = new ConcurrentHashMap<>();
        response.put("error", "Repository exception");
        response.put("message", e.getMessage());

        return new Message("Error", response);
    }

    @ExceptionHandler(EntityNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public final Message handleEntityNotFoundException(EntityNotFoundException e) {
        log.error("Service exception");

        ConcurrentHashMap<String, String> response = new ConcurrentHashMap<>();
        response.put("error", "Entity not found");
        response.put("message", e.getMessage());

        return new Message("Error", response);
    }
}
