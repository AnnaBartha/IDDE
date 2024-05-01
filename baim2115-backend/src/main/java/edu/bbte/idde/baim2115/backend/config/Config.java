package edu.bbte.idde.baim2115.backend.config;

import lombok.Data;

@Data
public class Config {
    private String profile;
    private String username;
    private String password;
    private Integer poolsize;
    private String jdbcDriver;
    private String database;
    private String url;
}
