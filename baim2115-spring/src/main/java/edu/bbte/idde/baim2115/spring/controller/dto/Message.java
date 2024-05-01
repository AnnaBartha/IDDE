package edu.bbte.idde.baim2115.spring.controller.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.concurrent.ConcurrentHashMap;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Message {
    

    private String errorMessage;

    private ConcurrentHashMap<String, String> messages;

    public Message(String errorMessage) {
        this.errorMessage = errorMessage;
    }

}
