package database.daos;

import database.Dao;
import database.DatabaseExecutionContext;
import models.Sighting;
import play.db.jpa.JPAApi;

import javax.inject.Inject;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.stream.Stream;

public class SightingDao extends Dao {

    @Inject
    public SightingDao(JPAApi jpaApi, DatabaseExecutionContext executionContext) {
        super(jpaApi, executionContext);
    }

    public CompletionStage<Sighting> save(Sighting sighting) {
        return CompletableFuture.supplyAsync(() -> wrap(em -> {
            em.persist(sighting);
            return sighting;
        }), executionContext);
    }

    public CompletionStage<Stream<Sighting>> getAll() {
        return CompletableFuture.supplyAsync(() -> wrap(em -> {
            return em.createQuery("SELECT sighting FROM Sighting sighting", Sighting.class)
                .getResultList().stream();
        }), executionContext);
    }

    public CompletionStage<Sighting> getById(String id) {
        return CompletableFuture.supplyAsync(() -> wrap(em -> {
            return em.find(Sighting.class, id);
        }), executionContext);
    }
}
