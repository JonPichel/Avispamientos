package database.daos;

import akka.japi.Pair;
import database.Dao;
import database.DatabaseExecutionContext;
import models.Sighting;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import play.db.jpa.JPAApi;

import javax.inject.Inject;
import java.util.Comparator;
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

    public CompletionStage<Stream<Sighting>> getNearSightings(double latitude, double longitude, int radius) {
        return CompletableFuture.supplyAsync(() -> wrap(em ->
            em.createQuery("SELECT sighting FROM Sighting sighting", Sighting.class)
                .getResultList().stream()
                .map(sighting -> new Pair<>(sighting, sighting.distanceToCoords(latitude, longitude)))
                .filter(pair -> pair.second() < radius)
                .sorted(Comparator.comparing(Pair::second))
                .map(Pair::first)
        ), executionContext);
    }

    public CompletionStage<Sighting> findById(String id) {
        return CompletableFuture.supplyAsync(() -> wrap(em -> {
            return em.find(Sighting.class, id);
        }), executionContext);
    }
}
