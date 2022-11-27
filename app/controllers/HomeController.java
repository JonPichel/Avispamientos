package controllers;

import database.daos.UserDao;
import models.User;
import play.libs.concurrent.HttpExecutionContext;
import play.mvc.*;

import javax.inject.Inject;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

public class HomeController extends Controller {

    private final UserDao userDao;
    private final HttpExecutionContext executionContext;

    @Inject
    public HomeController(UserDao userDao, HttpExecutionContext executionContext) {
        this.userDao = userDao;
        this.executionContext = executionContext;
    }

    public CompletionStage<Result> homePage(Http.Request request) {
        if (request.session().get("identity").isPresent()) {
            return userDao.findByName(request.session().get("identity").get()).thenApplyAsync(user -> {
                if (user == null) {
                    return ok(views.html.home.render(null))
                        .removingFromSession(request, "identity");
                } else {
                    return ok(views.html.home.render(user.getUsername()));
                }
            });
        }
        return CompletableFuture.supplyAsync(() -> ok(views.html.home.render(null)));
    }
}
