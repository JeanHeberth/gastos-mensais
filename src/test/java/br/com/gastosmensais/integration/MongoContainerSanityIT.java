package br.com.gastosmensais.integration;


import br.com.gastosmensais.config.AbstractIntegrationTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.mongodb.core.MongoTemplate;

import static org.assertj.core.api.Assertions.assertThat;


@SpringBootTest
public class MongoContainerSanityIT extends AbstractIntegrationTest {

    @Autowired
    MongoTemplate mongoTemplate;

    @BeforeEach
    void limpaCollection() {
        mongoTemplate.dropCollection("teste_sanity");
    }

    @Test
    void deveConectarAoMongoContainer() {

        // cria uma collection de teste e insere um dado
        var collection = mongoTemplate.getCollection("teste_sanity");
        collection.insertOne(new org.bson.Document("chave", "valor"));
        var count = collection.countDocuments();

        assertThat(count).isEqualTo(1);
    }

}
