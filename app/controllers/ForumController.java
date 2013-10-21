package controllers;

import models.forum.Forum;
import play.mvc.Controller;
import play.mvc.Result;

import java.util.List;

public class ForumController extends Controller {

    public static Result index() {
        List<Forum> forumList = Forum.find.where().eq("showForum", true).orderBy().asc("showOrder").findList();

        //We remove everything that this user don't have access

        return ok(views.html.forum.index.render(forumList));
    }
}
