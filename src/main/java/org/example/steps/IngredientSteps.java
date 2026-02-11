package org.example.steps;

import io.qameta.allure.Step;
import io.restassured.response.ValidatableResponse;

import java.util.List;
import java.util.stream.Collectors;

import static io.restassured.RestAssured.given;

public class IngredientSteps {

    private static final String GET_INGREDIENTS = "/api/ingredients";

    @Step("Получение списка всех ингредиентов")
    public ValidatableResponse getAllIngredients() {
        return given()
                .when()
                .get(GET_INGREDIENTS)
                .then();
    }

    @Step("Получение списка всех ID ингредиентов")
    public List<String> getAllIngredientIds() {
        ValidatableResponse response = getAllIngredients();
        return response
                .extract()
                .path("data._id");
    }

    @Step("Получение нескольких случайных ингредиентов")
    public List<String> getRandomIngredientIds(int count) {
        List<String> ids = getAllIngredientIds();

        return ids.stream()
                .limit(count)
                .collect(Collectors.toList());
    }
}

