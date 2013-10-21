package models.forum;

import com.avaje.ebean.validation.NotNull;
import enums.forum.ForumType;
import play.db.ebean.Model;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import java.util.List;

@Entity
public class Forum {

    @Id
    public Long id;

    @NotNull
    public String name;

    @NotNull
    public ForumType type;

    public Long showOrder;

    public boolean showForum;

    @ManyToOne
    public Forum parentId;

    @OneToMany
    public List<Forum> childs;

    @OneToMany
    public List<Topic> posts;

    public static final Model.Finder<String, Forum> find = new Model.Finder<String, Forum>(String.class, Forum.class);
}
