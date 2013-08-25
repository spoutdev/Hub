package models.resource;

import java.util.List;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToMany;

import play.data.validation.Constraints;
import play.db.ebean.Model;

@Entity
public class ResourceCategory extends Model {
	@Id
	public Long id;
	@Constraints.Required
	public String name;
	public String iconFile;
	@ManyToMany
	public List<Resource> resourceList;
	public boolean mainCategory; // Used to separate plugins from textures and such.
	public boolean secondaryCategory; // Used to separate plugins into sub categories.
}
