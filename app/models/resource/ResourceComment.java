package models.resource;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

import models.User;
import play.data.validation.Constraints;
import play.db.ebean.Model;

@Entity
public class ResourceComment extends Model {

    @Id
    public Long id;

    @Constraints.Required
    public String comment;

    @Constraints.Required
    @ManyToOne
    public User user;

    @ManyToOne
    public Resource resource; //If this is a comment on the main resource page

    @ManyToOne
    public ResourceTicket resourceTicket; //If this is a comment on a ticket
}
