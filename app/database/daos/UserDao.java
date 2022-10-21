package database.daos;

import database.Dao;
import database.DatabaseExecutionContext;
import models.User;
import play.db.jpa.JPAApi;

import javax.inject.Inject;
import javax.persistence.NoResultException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.stream.Stream;


public class UserDao extends Dao {

    @Inject
    public UserDao(JPAApi jpaApi, DatabaseExecutionContext executionContext) {
        super(jpaApi, executionContext);
    }

    public CompletionStage<User> save(User user) {
        return CompletableFuture.supplyAsync(() -> wrap(em -> {
            em.persist(user);
            return user;
        }), executionContext);
    }

    public CompletionStage<Stream<User>> getAll() {
        return CompletableFuture.supplyAsync(() -> wrap(em -> {
            return em.createQuery("SELECT user FROM User user", User.class)
                .getResultList().stream();
        }), executionContext);
    }

    public CompletionStage<User> getByName(String username) {
        return CompletableFuture.supplyAsync(() -> wrap(em -> {
            return em.find(User.class, username);
        }), executionContext);
    }

    public CompletionStage<User> getByNameAndPassword(String username, String password) {
        return CompletableFuture.supplyAsync(() -> wrap(em -> {
            try {
                return em.createQuery(
                        "SELECT user FROM User user WHERE user.username = :username AND user.password = :password", User.class
                    )
                    .setParameter("username", username)
                    .setParameter("password", password)
                    .getSingleResult();
            } catch (NoResultException exc) {
                return null;
            }
        }), executionContext);
    }
}