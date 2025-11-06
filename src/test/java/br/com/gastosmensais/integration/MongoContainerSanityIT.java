package br.com.gastosmensais.integration;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.assertj.core.api.Assertions.assertThat;


@Testcontainers
@SpringBootTest
public class MongoContainerSanityIT {

    @Container
    static MongoDBContainer mongo = new MongoDBContainer("mongo:latest");

    @Autowired
    private MongoTemplate mongoTemplate;

    @BeforeEach
    void limpaCollection() {
        mongoTemplate.dropCollection("teste_sanity");
    }

    @Test
    void deveConectarAoMongoContainer() {

        // sanity check
        assertThat(mongo.isRunning()).isTrue();

        // cria uma collection de teste e insere um dado
        var collection = mongoTemplate.getCollection("teste_sanity");
        collection.insertOne(new org.bson.Document("chave", "valor"));
        var count = collection.countDocuments();

        assertThat(count).isEqualTo(1);
    }

}
