package br.com.gastosmensais.config;


import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.utility.DockerImageName;

public abstract class AbstractIntegrationTest {

    private static final MongoDBContainer mongoDBContainer;

    @Autowired
    private MongoTemplate mongoTemplate;

    @BeforeEach
    void limparBanco() {
        mongoTemplate.getDb().drop();
    }

    static {
        mongoDBContainer = new MongoDBContainer(DockerImageName.parse("mongo:latest"))
                .withExposedPorts(27017)
                .withReuse(true);
        mongoDBContainer.start();
    }

    @DynamicPropertySource
    static void properties(DynamicPropertyRegistry registry) {
        registry.add("spring.data.mongodb.uri", mongoDBContainer::getReplicaSetUrl);
    }


}
