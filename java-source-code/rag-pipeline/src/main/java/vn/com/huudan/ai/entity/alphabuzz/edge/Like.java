package vn.com.huudan.ai.entity.alphabuzz.edge;

import java.time.OffsetDateTime;

import org.springframework.data.neo4j.core.schema.GeneratedValue;
import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.RelationshipProperties;
import org.springframework.data.neo4j.core.schema.TargetNode;

import vn.com.huudan.ai.entity.alphabuzz.node.Buzz;

@RelationshipProperties
public class Like {

    @Id
    @GeneratedValue
    private String id;

    @TargetNode
    private Buzz target;

    private OffsetDateTime likeAt;

    public Like() {
    }

    public Like(Buzz target, OffsetDateTime likeAt) {
        this.target = target;
        this.likeAt = likeAt;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Buzz getTarget() {
        return target;
    }

    public void setTarget(Buzz target) {
        this.target = target;
    }

    public OffsetDateTime getLikeAt() {
        return likeAt;
    }

    public void setLikeAt(OffsetDateTime likeAt) {
        this.likeAt = likeAt;
    }

}
