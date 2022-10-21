package controllers;

import akka.japi.Pair;
import database.daos.ConfirmationDao;
import database.daos.SightingDao;
import database.daos.UserDao;
import models.Confirmation;
import models.Sighting;
import models.User;
import play.libs.concurrent.HttpExecutionContext;
import play.mvc.Controller;
import play.mvc.Result;

import javax.inject.Inject;
import java.util.Objects;
import java.util.concurrent.CompletionStage;
import java.util.stream.Collectors;

import static play.libs.Json.toJson;

public class ConfirmationController extends Controller {

    private final UserDao userDao;
    private final SightingDao sightingDao;
    private final ConfirmationDao confirmationDao;
    private final HttpExecutionContext executionContext;

    @Inject
    public ConfirmationController(UserDao userDao, SightingDao sightingDao, ConfirmationDao confirmationDao, HttpExecutionContext executionContext) {
        this.userDao = userDao;
        this.sightingDao = sightingDao;
        this.confirmationDao = confirmationDao;
        this.executionContext = executionContext;
    }

    public CompletionStage<Result> create(String sightingId, String contributor) {
        System.out.println(sightingId + " " + contributor);
        return userDao.getByName(contributor)
            .thenCombineAsync(sightingDao.getById(sightingId), (user, sighting) -> new Pair(user, sighting))
            .thenApplyAsync(pair -> {
                User user = (User)pair.first();
                Sighting sighting = (Sighting)pair.second();
                if (user == null || sighting == null || Objects.equals(sighting.getCreator().getUsername(), user.getUsername())) {
                    return null;
                } else {
                    Confirmation confirmation = new Confirmation(user, sighting);
                    confirmationDao.save(confirmation);
                    return confirmation;
                }
            }, executionContext.current())
            .thenApplyAsync(created_confirmation -> {
                if (created_confirmation == null) {
                    return ok("Error creating confirmation!");
                } else {
                    return ok("Created confirmation: " + toJson(created_confirmation));
                }
            }, executionContext.current());
    }

    public CompletionStage<Result> getAll() {
        return confirmationDao
            .getAll()
            .thenApplyAsync(confirmationStream -> ok(toJson(confirmationStream.collect(Collectors.toList()))),
                executionContext.current());
    }
}
