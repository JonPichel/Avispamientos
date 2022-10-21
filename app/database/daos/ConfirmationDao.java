package database.daos;

import database.Dao;
import database.DatabaseExecutionContext;
import models.Confirmation;
import play.db.jpa.JPAApi;

import javax.inject.Inject;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.stream.Stream;

public class ConfirmationDao extends Dao {

    @Inject
    public ConfirmationDao(JPAApi jpaApi, DatabaseExecutionContext executionContext) {
        super(jpaApi, executionContext);
    }

    public CompletionStage<Confirmation> save(Confirmation confirmation) {
        return CompletableFuture.supplyAsync(() -> wrap(em -> {
            em.persist(confirmation);
            return confirmation;
        }), executionContext);
    }

    public CompletionStage<Stream<Confirmation>> getAll() {
        return CompletableFuture.supplyAsync(() -> wrap(em -> {
            return em.createQuery("SELECT confirmation FROM Confirmation confirmation", Confirmation.class)
                .getResultList().stream();
        }));
    }
}
