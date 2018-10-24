package imt.org.web.weatherdatabase.crud;

import imt.org.web.weatherdatabase.crud.facade.IEntityFacade;

import javax.persistence.*;
import java.io.Serializable;

/**
 * Generic CRUD facade implementation
 * @param <T> Entity type
 */
public class CRUDEntityFacade<T> implements IEntityFacade<T> {

    private static final EntityManagerFactory ENTITY_MANAGER_FACTORY = Persistence
            .createEntityManagerFactory("WeatherDatabase");

    /**
     * Insert object
     * @param entity Entity
     */
    @Override
    public void create(final T entity) {
        EntityManager manager = ENTITY_MANAGER_FACTORY.createEntityManager();
        EntityTransaction transaction = null;

        try {
            transaction = manager.getTransaction();
            transaction.begin();
            System.out.println("CRUD facade - create() - Begin transaction");

            manager.persist(entity);
            transaction.commit();
            System.out.println("CRUD facade - create() - Transaction success");
        } catch (PersistenceException hibernateEx) {
            System.out.println("CRUD facade - create() - Insert error - " + hibernateEx.getMessage());
            if (transaction != null) {
                transaction.rollback();
                System.out.println("CRUD facade - create() - Action rollback !\n" + hibernateEx.getMessage());
            }
        } finally {
            manager.close();
            System.out.println("CRUD facade - create() - EntityManager closed");
        }
    }

    /**
     * Select object
     * @param entity Entity class
     * @param primaryKey PrK
     * @return Requested object
     */
    @Override
    public T read(final Class<T> entity, final Serializable primaryKey) {
        EntityManager manager = ENTITY_MANAGER_FACTORY.createEntityManager();
        T entities = null;

        try {
            System.out.println("CRUD facade - read() - Begin read");
            entities = manager.find(entity, primaryKey);
            System.out.println("CRUD facade - read() - Read success");
        } catch (PersistenceException hibernateEx) {
            System.out.println("CRUD facade - read() - Read error - " + hibernateEx.getMessage());

        } finally {
            manager.close();
            System.out.println("CRUD facade - read() - EntityManager closed");
            return entities;
        }
    }
}
