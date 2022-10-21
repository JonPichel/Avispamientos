package controllers;

import database.daos.UserDao;
import models.User;
import play.data.FormFactory;
import play.libs.concurrent.HttpExecutionContext;
import play.mvc.Controller;
import play.mvc.Result;

import javax.inject.Inject;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.stream.Collectors;

import static play.libs.Json.toJson;

public class UserController extends Controller {

    private final FormFactory formFactory;
    private final UserDao userDao;
    private final HttpExecutionContext executionContext;

    @Inject
    public UserController(FormFactory formFactory, UserDao userDao, HttpExecutionContext executionContext) {
        this.formFactory = formFactory;
        this.userDao = userDao;
        this.executionContext = executionContext;
    }

    public CompletionStage<Result> create(String username, String password) {
        // User user = formFactory.form(User.class).bindFromRequest(request).get();
        return userDao.getByName(username)
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
                    return ok("User already exists!");
                } else {
                    return ok("User created: " + toJson(created_user));
                }
            }, executionContext.current());
    }

    public CompletionStage<Result> getAll() {
        return userDao
            .getAll()
            .thenApplyAsync(userStream -> ok(toJson(userStream.collect(Collectors.toList()))),
                executionContext.current());
    }

    public CompletionStage<Result> getByName(String username) {
        return userDao
            .getByName(username)
            .thenApplyAsync(user -> ok(toJson(user)), executionContext.current());
    }

    public CompletionStage<Result> login(String username, String password) {
        return userDao
            .getByNameAndPassword(username, password)
            .thenApplyAsync(user -> {
                if (user == null) {
                    return ok("Bad credentials");
                } else {
                    return ok("Logged in: " + toJson(user));
                }
            }, executionContext.current());
    }
}
