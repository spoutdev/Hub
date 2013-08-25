package models.resource;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

import enums.resource.ResourceRole;
import models.User;
import play.data.validation.Constraints;
import play.db.ebean.Model;

@Entity
public class ResourceACL extends Model {
	@Id
	public Long id;
	@Constraints.Required
	@ManyToOne
	public User user;
	@Constraints.Required
	@ManyToOne
	public Resource Resource;
	@Constraints.Required
	public ResourceRole resourceRole;
}
