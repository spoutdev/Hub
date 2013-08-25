package models.resource;

import java.util.List;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;

import enums.resource.ResourceStatus;
import play.data.validation.Constraints;
import play.db.ebean.Model;

@Entity
public class Resource extends Model {
	@Id
	public Long id;
	// The name of the resource.
	@Constraints.Required
	public String name;
	// The amount of time the page has been shown.
	public Long pageShown;
	// Rating of the resource.
	public double rating;
	public ResourceStatus status;
	public String donationEmail;
	@OneToMany
	public List<ResourceDownload> downloadList;
	@OneToMany
	public List<ResourcePage> resourcePagesList;
	@OneToMany
	public List<ResourceReview> resourceReviewList;
	@OneToMany
	public List<ResourceComment> resourceCommentList;
	@OneToMany
	public List<ResourceWatcher> resourceWatcherList;
	@ManyToMany
	public List<ResourceCategory> resourceCategoryList;
	@OneToMany
	public List<ResourceTicket> resourceTicketList;
}
