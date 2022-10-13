package controllers;

import database.daos.UserDao;
import models.User;
import play.data.FormFactory;
import play.libs.concurrent.HttpExecutionContext;
import play.mvc.Controller;
import play.mvc.Result;

import javax.inject.Inject;
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
        User user = new User(username, password);
        return userDao
            .save(user)
            .thenApplyAsync(u -> ok("User created!"));
    }

    public CompletionStage<Result> getAll() {
        return userDao
            .getAll()
            .thenApplyAsync(userStream -> ok(toJson(userStream.collect(Collectors.toList()))), executionContext.current());
    }

    public CompletionStage<Result> getByName(String username) {
        return userDao
            .getByName(username)
            .thenApplyAsync(user -> ok(toJson(user)), executionContext.current());
    }
}
