package controllers;

import play.mvc.*;

public class HomeController extends Controller {

    public Result homePage() {
        return ok(views.html.home.render());
    }
}
