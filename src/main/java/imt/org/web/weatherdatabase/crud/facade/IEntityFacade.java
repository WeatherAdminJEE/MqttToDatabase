package imt.org.web.weatherdatabase.crud.facade;

import java.io.Serializable;

/**
 * Generic CRUD facade interface
 * @param <T> Entity type
 */
public interface IEntityFacade<T> {

    /**
     * Insert object
     * @param entity Entity
     */
    void create(T entity);

    /**
     * Select object
     * @param entity Entity class
     * @param primaryKey PrK
     * @return Requested object
     */
    T read(Class<T> entity, Serializable primaryKey);
}
