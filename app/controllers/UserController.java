package controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import database.daos.UserDao;
import models.User;
import play.libs.concurrent.HttpExecutionContext;
import play.mvc.Controller;
import play.mvc.Http;
import play.mvc.Result;

import javax.inject.Inject;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.stream.Collectors;

import static play.libs.Json.toJson;

public class UserController extends Controller {

    private final UserDao userDao;
    private final HttpExecutionContext executionContext;

    @Inject
    public UserController(UserDao userDao, HttpExecutionContext executionContext) {
        this.userDao = userDao;
        this.executionContext = executionContext;
    }

    public Result loginPage(Http.Request request) {
        if (request.session().get("identity").isPresent()) {
            return redirect("/");
        }
        return ok(views.html.login.render(null, request));
    }

    public Result registerPage(Http.Request request) {
        if (request.session().get("identity").isPresent()) {
            return redirect("/");
        }
        return ok(views.html.register.render(null, request));
    }

    public Result unsubscribePage(Http.Request request) {
        return ok(views.html.unsubscribe.render(null, request));
    }

    public CompletionStage<Result> register(Http.Request request) {
        Map<String, String[]> params = request.body().asFormUrlEncoded();
        if (!params.containsKey("username") || !params.containsKey("password")) {
            return CompletableFuture.supplyAsync(() -> ok(views.html.register.render("Bad request", request)));
        }
        String username = params.get("username")[0];
        String password = params.get("password")[0];
        return userDao.findByName(username)
            .thenApplyAsync(existing_user -> {
                if (existing_user == null) {
                    User user = new User(username, password);
                    userDao.save(user);
                    return user;
                } else {
                    return null;
                }
            }, executionContext.current())
            .thenApplyAsync(created_user -> {
                if (created_user == null) {
                    return ok(views.html.login.render("User already exists", request));
                } else {
                    return redirect("/")
                        .addingToSession(request, "identity", created_user.getUsername());
                }
            }, executionContext.current());
    }

    public CompletionStage<Result> login(Http.Request request) {
        Map<String, String[]> params = request.body().asFormUrlEncoded();
        return userDao
            .findByNameAndPassword(params.get("username")[0], params.get("password")[0])
            .thenApplyAsync(user -> {
                if (user == null) {
                    return ok(views.html.login.render("Bad credentials", request));
                } else {
                    // TODO: Remove user from database
                    return redirect("/")
                        .addingToSession(request, "identity", user.getUsername());
                }
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
            .thenApplyAsync(existing_user -> {
                if (existing_user == null) {
                    return ok(views.html.unsubscribe.render("Bad credentials", request));
                } else {
                    return redirect("/").removingFromSession(request, "identity");
                }
            }, executionContext.current());
    }

    public Result logout(Http.Request request) {
        return redirect("/").removingFromSession(request, "identity");
    }

    public CompletionStage<Result> getAll() {
        return userDao
            .getAll()
            .thenApplyAsync(userStream -> ok(toJson(userStream.collect(Collectors.toList()))),
                executionContext.current());
    }

    public CompletionStage<Result> getByName(String username) {
        return userDao
            .findByName(username)
            .thenApplyAsync(user -> ok(toJson(user)), executionContext.current());
    }
}
