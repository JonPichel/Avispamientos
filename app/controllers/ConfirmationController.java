package controllers;

import akka.japi.Pair;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import database.daos.ConfirmationDao;
import database.daos.SightingDao;
import database.daos.UserDao;
import models.Confirmation;
import models.Sighting;
import models.User;
import play.libs.concurrent.HttpExecutionContext;
import play.mvc.Controller;
import play.mvc.Http;
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

    public CompletionStage<Result> create(Http.Request request) {
        ObjectMapper mapper = new ObjectMapper();
        ObjectNode response = mapper.createObjectNode();

        JsonNode jsonBody = request.body().asJson();
        String contributor = jsonBody.get("contributor").asText();
        String sightingId = jsonBody.get("sighting").asText();
        return userDao.findByName(contributor)
            .thenCombineAsync(sightingDao.findById(sightingId), (user, sighting) -> new Pair(user, sighting))
            .thenApplyAsync(pair -> {
                User user = (User) pair.first();
                Sighting sighting = (Sighting) pair.second();
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
                    response.put("result", "ERROR");
                    return ok(toJson(response));
                } else {
                    response.set("result", toJson(created_confirmation));
                    return ok(toJson(response));
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
