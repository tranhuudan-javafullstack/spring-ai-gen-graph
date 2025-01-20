package vn.com.huudan.ai.entity.alphabuzz.edge;

import java.time.LocalDate;

import org.springframework.data.neo4j.core.schema.GeneratedValue;
import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.RelationshipProperties;
import org.springframework.data.neo4j.core.schema.TargetNode;

import vn.com.huudan.ai.entity.alphabuzz.node.User;

@RelationshipProperties
public class Follow {

    @Id
    @GeneratedValue
    private String id;

    @TargetNode
    private User target;

    private LocalDate since;

    public Follow() {
    }

    public Follow(User target, LocalDate since) {
        this.target = target;
        this.since = since;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public User getTarget() {
        return target;
    }

    public void setTarget(User target) {
        this.target = target;
    }

    public LocalDate getSince() {
        return since;
    }

    public void setSince(LocalDate since) {
        this.since = since;
    }

}
