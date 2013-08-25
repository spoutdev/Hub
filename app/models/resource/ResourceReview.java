package models.resource;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

import models.User;
import play.data.validation.Constraints;
import play.db.ebean.Model;

@Entity
public class ResourceReview extends Model {
	@Id
	public Long id;
	@Constraints.Required
	@Constraints.Min (0)
	@Constraints.Max (5)
	public int stars; // Contains the review star amount (out of 5?).
	@Column (length = 500)
	public String review;
	@ManyToOne
	public Resource resource;
	@ManyToOne
	public User reviewer;
}
