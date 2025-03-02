package com.eventmgmt.dao;

import com.eventmgmt.model.BaseEntity;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;
import javax.persistence.Query;

import java.lang.reflect.ParameterizedType;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * Base class for Data Access Objects (DAOs) that provides common operations.
 * 
 * @param <T>  The entity type
 * @param <ID> The ID type of the entity
 */
public abstract class BaseDAO<T extends BaseEntity, ID> {

    private static final EntityManagerFactory emf = Persistence.createEntityManagerFactory("eventmgmtPU");

    private final Class<T> entityClass;

    /**
     * Constructor that determines the entity class type for generic operations.
     */
    @SuppressWarnings("unchecked")
    public BaseDAO() {
        this.entityClass = (Class<T>) ((ParameterizedType) getClass()
                .getGenericSuperclass()).getActualTypeArguments()[0];
    }

    /**
     * Creates a new EntityManager.
     * 
     * @return A new EntityManager instance
     */
    protected EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    /**
     * Executes a function within a transaction and returns a result.
     * 
     * @param function The function to execute
     * @return The result of the function
     */
    protected <R> R executeInTransaction(Function<EntityManager, R> function) {
        EntityManager em = getEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            R result = function.apply(em);
            tx.commit();
            return result;
        } catch (Exception e) {
            if (tx.isActive()) {
                tx.rollback();
            }
            throw new RuntimeException("Transaction failed", e);
        } finally {
            em.close();
        }
    }

    /**
     * Executes an action within a transaction.
     * 
     * @param action The action to execute
     */
    protected void executeWithoutResult(Consumer<EntityManager> action) {
        EntityManager em = getEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            action.accept(em);
            tx.commit();
        } catch (Exception e) {
            if (tx.isActive()) {
                tx.rollback();
            }
            throw new RuntimeException("Transaction failed", e);
        } finally {
            em.close();
        }
    }

    /**
     * Saves a new entity or updates an existing one.
     * 
     * @param entity The entity to save or update
     * @return The saved or updated entity
     */
    public T save(T entity) {
        return executeInTransaction(em -> {
            if (entity.getId() == null) {
                em.persist(entity);
                return entity;
            } else {
                return em.merge(entity);
            }
        });
    }

    /**
     * Finds an entity by its ID.
     * 
     * @param id The ID of the entity to find
     * @return An Optional containing the entity if found, or empty if not found
     */
    public Optional<T> findById(ID id) {
        return executeInTransaction(em -> Optional.ofNullable(em.find(entityClass, id)));
    }

    /**
     * Retrieves all entities of the managed type.
     * 
     * @return A list of all entities
     */
    @SuppressWarnings("unchecked")
    public List<T> findAll() {
        return executeInTransaction(em -> {
            String jpql = "SELECT e FROM " + entityClass.getSimpleName() + " e";
            Query query = em.createQuery(jpql);
            return query.getResultList();
        });
    }

    /**
     * Retrieves all entities with pagination.
     * 
     * @param offset The offset for pagination
     * @param limit  The maximum number of results to return
     * @return A list of entities for the requested page
     */
    @SuppressWarnings("unchecked")
    public List<T> findAll(int offset, int limit) {
        return executeInTransaction(em -> {
            String jpql = "SELECT e FROM " + entityClass.getSimpleName() + " e";
            Query query = em.createQuery(jpql);
            query.setFirstResult(offset);
            query.setMaxResults(limit);
            return query.getResultList();
        });
    }

    /**
     * Counts the total number of entities.
     * 
     * @return The total count of entities
     */
    public Long count() {
        return executeInTransaction(em -> {
            String jpql = "SELECT COUNT(e) FROM " + entityClass.getSimpleName() + " e";
            Query query = em.createQuery(jpql);
            return (Long) query.getSingleResult();
        });
    }

    /**
     * Deletes an entity by its ID.
     * 
     * @param id The ID of the entity to delete
     * @return true if the entity was found and deleted, false otherwise
     */
    public boolean deleteById(ID id) {
        return executeInTransaction(em -> {
            T entity = em.find(entityClass, id);
            if (entity != null) {
                em.remove(entity);
                return true;
            }
            return false;
        });
    }

    /**
     * Deletes an entity.
     * 
     * @param entity
     */
    public void delete(T entity) {
        executeWithoutResult(em -> {
            T managedEntity = em.merge(entity);
            em.remove(managedEntity);
        });
    }

    /**
     * Refreshes an entity from the database.
     * 
     * @param entity The entity to refresh
     * @return The refreshed entity
     */
    public T refresh(T entity) {
        return executeInTransaction(em -> {
            em.refresh(entity);
            return entity;
        });
    }

    /**
     * Executes a named query and returns the results.
     * 
     * @param queryName The name of the query
     * @param params    Parameters for the query in name-value pairs
     * @return The list of results
     */
    @SuppressWarnings("unchecked")
    protected List<T> executeNamedQuery(String queryName, Object... params) {
        return executeInTransaction(em -> {
            Query query = em.createNamedQuery(queryName);
            for (int i = 0; i < params.length; i += 2) {
                query.setParameter(params[i].toString(), params[i + 1]);
            }
            return query.getResultList();
        });
    }

    /**
     * Executes a JPQL query and returns the results.
     * 
     * @param jpql   The JPQL query string
     * @param params Parameters for the query in name-value pairs
     * @return The list of results
     */
    @SuppressWarnings("unchecked")
    protected List<T> executeQuery(String jpql, Object... params) {
        return executeInTransaction(em -> {
            Query query = em.createQuery(jpql);
            for (int i = 0; i < params.length; i += 2) {
                query.setParameter(params[i].toString(), params[i + 1]);
            }
            return query.getResultList();
        });
    }

    public void clear() {
        executeWithoutResult(EntityManager::clear);
    }

    public void flush() {
        executeWithoutResult(EntityManager::flush);
    }
}