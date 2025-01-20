package vn.com.huudan.ai.entity.alphabuzz.node;

import java.util.UUID;

import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.Node;

@Node("Tag")
public class Tag {

    @Id
    private UUID tagId;

    private String text;

    public Tag() {
    }

    public Tag(UUID tagId, String text) {
        this.tagId = tagId;
        this.text = text;
    }

    public UUID getTagId() {
        return tagId;
    }

    public void setTagId(UUID tagId) {
        this.tagId = tagId;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

}
