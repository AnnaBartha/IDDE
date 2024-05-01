package edu.bbte.idde.baim2115.backend.repository;

public class RepositoryExeption extends RuntimeException {
    public RepositoryExeption() {
        super();
    }

    public RepositoryExeption(String message) {
        super(message);
    }

    public RepositoryExeption(String message, Throwable cause) {
        super(message, cause);
    }
}
