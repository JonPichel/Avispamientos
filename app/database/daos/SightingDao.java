package database.daos;

import database.Dao;
import database.DatabaseExecutionContext;
import models.Sighting;
import play.db.jpa.JPAApi;

import javax.inject.Inject;
import java.util.List;
import java.util.concurrent.CompletionStage;
import java.util.stream.Stream;

import static java.util.concurrent.CompletableFuture.supplyAsync;

public class SightingDao extends Dao {

    @Inject
    public SightingDao(JPAApi jpaApi, DatabaseExecutionContext executionContext) {
        super(jpaApi, executionContext);
    }

    public CompletionStage<Sighting> save(Sighting sighting) {
        return supplyAsync(() -> wrap(em -> {
            em.persist(sighting);
            return sighting;
        }), executionContext);
    }

    public CompletionStage<Stream<Sighting>> getAll() {
        return supplyAsync(() -> wrap(em -> {
            List<Sighting> sightings = em.createQuery("SELECT sighting FROM Sighting sighting", Sighting.class).getResultList();
            return sightings.stream();
        }), executionContext);
    }
}
