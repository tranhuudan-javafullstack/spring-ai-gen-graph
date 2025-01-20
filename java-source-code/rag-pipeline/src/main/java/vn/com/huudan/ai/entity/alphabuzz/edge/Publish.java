package vn.com.huudan.ai.entity.alphabuzz.edge;

import org.springframework.data.neo4j.core.schema.GeneratedValue;
import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.RelationshipProperties;
import org.springframework.data.neo4j.core.schema.TargetNode;

import vn.com.huudan.ai.entity.alphabuzz.node.Buzz;

@RelationshipProperties
public class Publish {

    @Id
    @GeneratedValue
    private String id;

    @TargetNode
    private Buzz target;

    public Publish() {
    }

    public Publish(Buzz target) {
        this.target = target;
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
}
