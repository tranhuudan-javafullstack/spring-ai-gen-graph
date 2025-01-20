package vn.com.huudan.ai.entity.alphabuzz.edge;

import java.time.OffsetDateTime;

import org.springframework.data.neo4j.core.schema.GeneratedValue;
import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.RelationshipProperties;
import org.springframework.data.neo4j.core.schema.TargetNode;

import vn.com.huudan.ai.entity.alphabuzz.node.Buzz;

@RelationshipProperties
public class Republish {

    @Id
    @GeneratedValue
    private String id;

    @TargetNode
    private Buzz target;

    private OffsetDateTime republishedAt;

    public Republish() {
    }

    public Republish(Buzz target, OffsetDateTime republishedAt) {
        this.target = target;
        this.republishedAt = republishedAt;
    }

    public String getId() {
        return id;
    }

    public Buzz getTarget() {
        return target;
    }

    public void setTarget(Buzz target) {
        this.target = target;
    }

    public OffsetDateTime getRepublishedAt() {
        return republishedAt;
    }

    public void setRepublishedAt(OffsetDateTime republishedAt) {
        this.republishedAt = republishedAt;
    }

}
