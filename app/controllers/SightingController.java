package controllers;

import database.daos.SightingDao;
import database.daos.UserDao;
import models.Sighting;
import models.User;
import play.mvc.Result;
import play.libs.concurrent.HttpExecutionContext;

import javax.inject.Inject;
import java.util.concurrent.CompletionStage;
import java.util.stream.Collectors;

import static play.libs.Json.toJson;
import static play.mvc.Results.ok;

public class SightingController {

    private final UserDao userDao;
    private final SightingDao sightingDao;
    private final HttpExecutionContext executionContext;

    @Inject
    public SightingController(UserDao userDao, SightingDao sightingDao, HttpExecutionContext executionContext) {
        this.userDao = userDao;
        this.sightingDao = sightingDao;
        this.executionContext = executionContext;
    }

    public CompletionStage<Result> getAll() {
        return sightingDao
            .getAll()
            .thenApplyAsync(userStream -> ok(toJson(userStream.collect(Collectors.toList()))), executionContext.current());
    }

    public Result create(double latitude, double longitude, String creator) {
        User user = userDao.getByName(creator).toCompletableFuture().join();

        Sighting sighting = new Sighting("", latitude, longitude, user);
        sightingDao.save(sighting).toCompletableFuture().join();

        return ok(toJson(sighting));
    }
}