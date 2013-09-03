package models.resource;

import java.util.ArrayList;
import java.util.List;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;

import com.avaje.ebean.Page;
import enums.resource.ResourceRole;
import enums.resource.ResourceStatus;
import models.User;
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
	@OneToMany
	public List<ResourceACL> resourceACLs;
	public static final Finder<Long, Resource> find = new Finder<Long, Resource>(Long.class, Resource.class);

	public static Page<Resource> page(int page, int pageSize, String sortBy, String order, String filter) {
		return find.where().ilike("name", "%" + filter + "%").orderBy(sortBy + " " + order).findPagingList(pageSize).getPage(page);
	}

	public List<User> getAuthors() {
		List<User> userList = new ArrayList<>();
		for (ResourceACL acl : resourceACLs) {
			if (acl.resourceRole.equals(ResourceRole.AUTHOR) || acl.resourceRole.equals(ResourceRole.CONTRIBUTOR)) {
				userList.add(acl.user);
			}
		}
		return userList;
	}
}
