package models.forum;

import models.User;
import play.db.ebean.Model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import java.util.Date;

@Entity
public class Post {

    @Id
    public Long id;

    @ManyToOne
    public User user;

    @ManyToOne
    public Topic topic;

    public Date date;

    public String title;

    @Column(length = 10000)
    public String message;

    public static final Model.Finder<String, Post> find = new Model.Finder<String, Post>(String.class, Post.class);
}
