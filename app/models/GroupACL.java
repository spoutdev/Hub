package models;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

import be.objectify.deadbolt.core.models.Permission;
import enums.ACLValue;
import play.data.validation.Constraints;
import play.db.ebean.Model;

@Entity
public class GroupACL extends Model implements Permission {
    @Id
    public Integer id;
    @Constraints.Required
    public String path;
    @Constraints.Required
    @ManyToOne(cascade = CascadeType.ALL)
    public SecurityRole securityRole;
    @Constraints.Required
    public ACLValue permission;

    @Override
    public String getValue() {
        return path;
    }
}
