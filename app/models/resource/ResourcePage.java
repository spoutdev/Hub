package models.resource;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

import models.User;
import play.data.validation.Constraints;
import play.db.ebean.Model;

@Entity
public class ResourcePage extends Model {

    @Id
    public Long id;

    @Constraints.Required
    public String name;

    @Constraints.Required
    @Column(length = 10000)
    public String content;

    @ManyToOne
    public User author;

    @ManyToOne
    public Resource resource;
}
