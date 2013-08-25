package models.resource;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

import enums.resource.ResourceDownloadStatus;
import play.data.validation.Constraints;
import play.db.ebean.Model;

@Entity
public class ResourceDownload extends Model {

    @Id
    public Long id;

    @Constraints.Required
    public String name;

    @Constraints.Required
    @Column(length = 2000)
    public String description;

    @Column(length = 2000)
    public String canevats;

    public ResourceDownloadStatus status;

    @Constraints.Required
    public String filename;

    @ManyToOne
    public Resource resource;
}
