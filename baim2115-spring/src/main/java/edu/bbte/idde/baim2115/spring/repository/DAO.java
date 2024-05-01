package edu.bbte.idde.baim2115.spring.repository;

import edu.bbte.idde.baim2115.spring.model.BaseEntity;

import java.util.Collection;
import java.util.Optional;

public interface DAO<T extends BaseEntity> {
    Collection<T> findAll();

    Optional<T> findById(Long id); //T-> Optional<T>

    T saveAndFlush(T entity); //saveAndFlush

    void update(T entity, Long id);

    void deleteById(Long id);
}
