package org.example.steps;

import io.qameta.allure.Step;
import io.restassured.response.ValidatableResponse;
import org.example.model.Order;
import java.util.List;
import static io.restassured.RestAssured.given;

public class OrderSteps {

    private static final String CREATE_ORDER = "/api/orders";
    private String accessToken;

    public void setAccessToken(String token) {
        this.accessToken = token;
    }

    @Step("Создание заказа с ингредиентами с авторизацией)")
    public ValidatableResponse createOrderWithAuth(List<String> ingredientIds) {
        Order order = new Order(ingredientIds);
        return given()
                .header("Authorization", accessToken)
                .body(order)
                .when()
                .post(CREATE_ORDER)
                .then();
    }

    @Step("Создание заказа без авторизации")
    public ValidatableResponse createOrderWithoutAuth(List<String> ingredientIds) {
        Order order = new Order(ingredientIds);
        return given()
                .body(order)
                .when()
                .post(CREATE_ORDER)
                .then();
    }

    @Step("Создание заказа без ингредиентов")
    public ValidatableResponse createOrderWithoutIngredients(boolean auth) {
        Order order = new Order();
        ValidatableResponse response = given()
                .body(order)
                .when()
                .post(CREATE_ORDER)
                .then();
        if(auth) {
            response = given()
                    .header("Authorization", accessToken)
                    .body(order)
                    .when()
                    .post(CREATE_ORDER)
                    .then();
        }
        return response;
    }

    @Step("Создание заказа с неверными хешами ингредиентов")
    public ValidatableResponse createOrderWithInvalidIngredients(List<String> invalidIds, boolean auth) {
        Order order = new Order(invalidIds);
        if(auth) {
            return given()
                    .header("Authorization", accessToken)
                    .body(order)
                    .when()
                    .post(CREATE_ORDER)
                    .then();
        } else {
            return given()
                    .body(order)
                    .when()
                    .post(CREATE_ORDER)
                    .then();
        }
    }
}
