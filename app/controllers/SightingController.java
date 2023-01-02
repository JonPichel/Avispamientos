package controllers;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import database.daos.SightingDao;
import database.daos.UserDao;
import models.Sighting;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import play.mvc.Controller;
import play.mvc.Http;
import play.mvc.Result;
import play.libs.concurrent.HttpExecutionContext;

import javax.inject.Inject;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.stream.Collectors;

import static play.libs.Json.toJson;

public class SightingController extends Controller {

    private final UserDao userDao;
    private final SightingDao sightingDao;
    private final HttpExecutionContext executionContext;

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Inject
    public SightingController(UserDao userDao, SightingDao sightingDao, HttpExecutionContext executionContext) {
        this.userDao = userDao;
        this.sightingDao = sightingDao;
        this.executionContext = executionContext;
        logger.warn("Testing");
    }

    public CompletionStage<Result> getNearSightings(double latitude, double longitude, int radius) {
        ObjectMapper mapper = new ObjectMapper();
        ObjectNode response = mapper.createObjectNode();

        if (latitude < -90 || latitude > 90 || longitude < -180 || longitude > 180 || radius <= 0) {
            response.put("error", "Bad request");
            return CompletableFuture.supplyAsync(() -> ok(toJson(response)));
        }
        return sightingDao.getNearSightings(latitude, longitude, radius)
            .thenApplyAsync(sightingStream -> ok(toJson(sightingStream.collect(Collectors.toList()))),
                executionContext.current());
    }

    public CompletionStage<Result> create(Http.Request request) {
        ObjectMapper mapper = new ObjectMapper();
        ObjectNode response = mapper.createObjectNode();

        JsonNode jsonBody = request.body().asJson();
        double latitude = jsonBody.get("latitude").asDouble();
        double longitude = jsonBody.get("longitude").asDouble();
        if (latitude < -90 || latitude > 90 || longitude < -180 || longitude > 180) {
            response.put("error", "Bad request");
            return CompletableFuture.supplyAsync(() -> ok(toJson(response)));
        }

        if (request.session().get("identity").isEmpty()) {
            response.put("error", "Unauthorized");
            return CompletableFuture.supplyAsync(() -> ok(toJson(response)));
        }
        String creator = request.session().get("identity").get();

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
                    response.put("error", "Creator not found: " + creator);
                } else {
                    response.put("sighting", toJson(created_sighting).toString());
                }
                return ok(toJson(response));
            }, executionContext.current());
    }

    public CompletionStage<Result> updateSighting(Http.Request request) {
        ObjectMapper mapper = new ObjectMapper();
        ObjectNode response = mapper.createObjectNode();

        JsonNode jsonBody = request.body().asJson();
        String sightingId = jsonBody.get("sightingId").asText();
        String information = jsonBody.get("information").asText();

        if (request.session().get("identity").isEmpty()) {
            response.put("error", "Unauthorized");
            return CompletableFuture.supplyAsync(() -> ok(toJson(response)));
        }

        String identity = request.session().get("identity").get();

        return sightingDao
            .findById(sightingId)
            .thenApplyAsync(sighting -> {
                if (sighting == null) {
                    response.put("error", "Sighting not found");
                    return ok(toJson(response));
                }
                if (!Objects.equals(sighting.getCreatorUsername(), identity)) {
                    response.put("error", "Unauthorized");
                    return ok(toJson(response));
                }
                sighting.setInformation(information);
                sightingDao.update(sighting);
                response.put("information", sighting.getInformation());
                return ok(toJson(response));
            }, executionContext.current());
    }

    public CompletionStage<Result> unsubscribe(Http.Request request) {
        if (request.session().get("identity").isEmpty()) {
            return CompletableFuture.supplyAsync(() ->
                ok(views.html.unsubscribe.render("Unauthorized: Login before trying again", request)));
        }

        Map<String, String[]> params = request.body().asFormUrlEncoded();
        if (!params.containsKey("username") || !params.containsKey("password")) {
            return CompletableFuture.supplyAsync(() ->
                ok(views.html.unsubscribe.render("Bad request", request)));
        }
        String username = params.get("username")[0];
        String password = params.get("password")[0];

        if (!request.session().get("identity").get().equals(username)) {
            return CompletableFuture.supplyAsync(() ->
                ok(views.html.unsubscribe.render("Unauthorized: Login before trying again!", request)));
        }

        return userDao.findByNameAndPassword(username, password)
            .thenApplyAsync(existingUser -> {
                if (existingUser == null) {
                    return ok(views.html.unsubscribe.render("Bad credentials", request));
                } else {
                    userDao.delete(existingUser);
                    return redirect("/").removingFromSession(request, "identity");
                }
            }, executionContext.current());
    }

    public CompletionStage<Result> deleteSighting(Http.Request request, String id) {
        ObjectMapper mapper = new ObjectMapper();
        ObjectNode response = mapper.createObjectNode();
        if (request.session().get("identity").isEmpty()) {
            response.put("error", "Unauthorized");
            return CompletableFuture.supplyAsync(() -> ok(toJson(response)));
        }
        String identity = request.session().get("identity").get();

        return sightingDao.findById(id)
            .thenApplyAsync(sighting -> {
                if (sighting == null) {
                    response.put("error", "Sighting not found");
                    return ok(toJson(response));
                }
                if (!Objects.equals(sighting.getCreatorUsername(), identity)) {
                    response.put("error", "Unauthorized");
                    return ok(toJson(response));
                }
                sightingDao.delete(sighting);
                response.put("id", id);
                return ok(toJson(response));
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
