package edu.bbte.idde.baim2115.backend.repository;

import edu.bbte.idde.baim2115.backend.model.BaseEntity;

import java.util.Collection;

// ide kerulnek azon muveletek, amelyek minden entitasra ervenyesek: findAll, findById
public interface Dao<T extends BaseEntity> {

    Collection<T> findAll();

    T findById(Long id);

    T create(T entity);

    T update(T entity, Long id);

    void delete(Long id);


}
