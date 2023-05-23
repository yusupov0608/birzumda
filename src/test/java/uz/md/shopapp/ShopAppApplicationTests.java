package uz.md.shopapp;

import com.github.javafaker.Faker;
import org.junit.jupiter.api.Test;
import org.springframework.test.context.ActiveProfiles;

@IntegrationTest
@ActiveProfiles("test")
class ShopAppApplicationTests {

    private final Faker FAKER = new Faker();

    @Test
    void contextLoads() {

    }

}
