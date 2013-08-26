package models;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import securesocial.core.AuthenticationMethod;

import models.resource.ResourceACL;
import models.resource.ResourceComment;
import models.resource.ResourcePage;
import models.resource.ResourceReview;
import models.resource.ResourceTicket;
import models.resource.ResourceWatcher;
import play.db.ebean.Model;

/**
 * Initial version based on work by Steve Chaloner (steve@objectify.be) for Deadbolt2.
 */
@Entity
@Table (name = "users")
public class User extends Model {
	private static final long serialVersionUID = 1L;
	@Id
	public String uuid;
	public String provider;
	public String firstName;
	public String lastName;
	public String email;
	public String password;
	// OAuth1 information
	public String oauth1Token;
	public String oauth1Secret;
	// OAuth2 information
	public String oauth2accessToken;
	public String oauth2tokenType;
	public Integer oauth2expiresIn;
	public String oauth2refreshToken;
	public AuthenticationMethod authMethod;
	public String clientAuthToken;
	public Date clientAuthTimeout;
	public String avatarURL;
	@ManyToMany
	public List<SecurityRole> roles;
	@OneToMany
	public List<ResourceACL> resourceACLs; // Contains all the access this user has on projects.
	@OneToMany
	public List<ResourceComment> resourceComments;
	@OneToMany
	public List<ResourceTicket> resourceTickets;
	@OneToMany
	public List<ResourcePage> resourcePages;
	@OneToMany
	public List<ResourceWatcher> resourceWatchers;
	@OneToMany
	public List<ResourceReview> resourceReviews;
	public static final Finder<String, User> find = new Finder<String, User>(String.class, User.class);

	public List<GroupACL> getPermissions() {
		List<GroupACL> permissions = new ArrayList<GroupACL>();
		for (SecurityRole role : roles) {
			permissions.addAll(role.permissions);
		}
		return permissions;
	}
}
