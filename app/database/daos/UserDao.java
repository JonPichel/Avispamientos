package database.daos;

import database.Dao;
import database.DatabaseExecutionContext;
import models.User;
import play.db.jpa.JPAApi;

import javax.inject.Inject;
import java.util.List;
import java.util.concurrent.CompletionStage;
import java.util.stream.Stream;

import static java.util.concurrent.CompletableFuture.supplyAsync;

public class UserDao extends Dao {

    @Inject
    public UserDao(JPAApi jpaApi, DatabaseExecutionContext executionContext) {
        super(jpaApi, executionContext);
    }

    public CompletionStage<User> save(User user) {
        return supplyAsync(() -> wrap(em -> {
            em.persist(user);
            return user;
        }), executionContext);
    }

    public CompletionStage<Stream<User>> getAll() {
        return supplyAsync(() -> wrap(em -> {
            List<User> users = em.createQuery("SELECT user FROM User user",User.class).getResultList();
            return users.stream();
        }), executionContext);
    }

    public CompletionStage<User> getByName(String username) {
        return supplyAsync(() -> wrap(em -> {
            User user = em.find(User.class, username);
            return user;
        }), executionContext);
    }
}