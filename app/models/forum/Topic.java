package models.forum;

import models.User;
import play.db.ebean.Model;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import java.util.List;

@Entity
public class Topic {

    @Id
    public Long id;

    @ManyToOne
    public Forum forum;

    @ManyToOne
    public User user;

    public String title;

    @OneToMany
    public List<Post> posts;

    public static final Model.Finder<String, Topic> find = new Model.Finder<String, Topic>(String.class, Topic.class);
}
