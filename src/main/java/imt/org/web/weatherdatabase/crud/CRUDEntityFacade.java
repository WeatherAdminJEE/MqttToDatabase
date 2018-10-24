package imt.org.web.weatherdatabase.crud;

import imt.org.web.weatherdatabase.crud.facade.IEntityFacade;
import imt.org.web.weatherdatabase.main.Main;

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
            Main.log.debug("CRUD facade - create() - Begin transaction");

            manager.persist(entity);
            transaction.commit();
            Main.log.debug("CRUD facade - create() - Transaction success");
        } catch (PersistenceException hibernateEx) {
            Main.log.debug("CRUD facade - create() - Insert error - " + hibernateEx.getMessage());
            if (transaction != null) {
                transaction.rollback();
                Main.log.debug("CRUD facade - create() - Action rollback !\n" + hibernateEx.getMessage());
            }
        } finally {
            manager.close();
            Main.log.debug("CRUD facade - create() - EntityManager closed");
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
            Main.log.debug("CRUD facade - read() - Begin read");
            entities = manager.find(entity, primaryKey);
            Main.log.debug("CRUD facade - read() - Read success");
        } catch (PersistenceException hibernateEx) {
            Main.log.debug("CRUD facade - read() - Read error - " + hibernateEx.getMessage());

        } finally {
            manager.close();
            Main.log.debug("CRUD facade - read() - EntityManager closed");
            return entities;
        }
    }
}
