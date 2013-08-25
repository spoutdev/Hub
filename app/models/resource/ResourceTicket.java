package models.resource;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import java.util.List;

import enums.resource.ResourceTicketPriority;
import enums.resource.ResourceTicketStatus;
import enums.resource.ResourceTicketType;
import models.User;
import play.data.validation.Constraints;
import play.db.ebean.Model;

@Entity
public class ResourceTicket extends Model {

    @Id
    public Long id;

    @Constraints.Required
    public String title;

    @Constraints.Required
    @ManyToOne
    public User submitter;

    @Constraints.Required
    public ResourceTicketStatus status;

    @Constraints.Required
    public ResourceTicketType type;

    @Constraints.Required
    public ResourceTicketPriority priority;

    @OneToMany
    public List<ResourceComment> resourceCommentList;

	@ManyToOne
	public Resource resource;
}
