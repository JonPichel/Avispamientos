package controllers;

import database.daos.SightingDao;
import database.daos.UserDao;
import models.Sighting;
import play.mvc.Controller;
import play.mvc.Result;
import play.libs.concurrent.HttpExecutionContext;

import javax.inject.Inject;
import java.util.concurrent.CompletionStage;
import java.util.stream.Collectors;

import static play.libs.Json.toJson;
import static play.mvc.Results.ok;

public class SightingController extends Controller {

    private final UserDao userDao;
    private final SightingDao sightingDao;
    private final HttpExecutionContext executionContext;

    @Inject
    public SightingController(UserDao userDao, SightingDao sightingDao, HttpExecutionContext executionContext) {
        this.userDao = userDao;
        this.sightingDao = sightingDao;
        this.executionContext = executionContext;
    }

    public CompletionStage<Result> create(double latitude, double longitude, String creator) {
        return userDao.findByName(creator).thenApplyAsync(user -> {
                if (user == null) {
                    return null;
                } else {
                    Sighting sighting = new Sighting("", latitude, longitude, user);
                    sightingDao.save(sighting);
                    return sighting;
                }
            }, executionContext.current())
            .thenApplyAsync(created_sighting -> {
                if (created_sighting == null) {
                    return ok("Creator not found: " + creator);
                } else {
                    return ok("Created sighting: " + toJson(created_sighting));
                }
            }, executionContext.current());
    }

    public CompletionStage<Result> getAll() {
        return sightingDao
            .getAll()
            .thenApplyAsync(sightingStream -> ok(toJson(sightingStream.collect(Collectors.toList()))),
                executionContext.current());
    }

    public CompletionStage<Result> getCreator(String sightingId) {
        return sightingDao
            .findById(sightingId)
            .thenApplyAsync(sighting -> {
               if (sighting == null) {
                   return ok("Sighting not found!");
               } else {
                   return ok(sighting.getCreatorUsername());
               }
            });
    }
}
