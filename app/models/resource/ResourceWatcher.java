package models.resource;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

import models.User;
import play.data.validation.Constraints;
import play.db.ebean.Model;

@Entity
public class ResourceWatcher extends Model {
	@Id
	public Long id;
	@Constraints.Required
	@ManyToOne
	public User user;
	@Constraints.Required
	@ManyToOne
	public Resource resource;
	public boolean updates;
	public boolean tickets;
	public boolean comments;
	public boolean forumPost;
}
