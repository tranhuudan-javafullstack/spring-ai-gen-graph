package vn.com.huudan.ai.repository.alphabuzz;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.neo4j.repository.query.Query;
import org.springframework.data.repository.query.Param;

import vn.com.huudan.ai.entity.alphabuzz.node.Tag;

public interface Neo4jTagRepository extends Neo4jRepository<Tag, UUID> {

    @Query("""
            MATCH (t:Tag)
            WHERE toLower(t.text) = toLower($text)
            RETURN t
            LIMIT 1
            """)
    Optional<Tag> findByText(@Param("text") String text);

}
