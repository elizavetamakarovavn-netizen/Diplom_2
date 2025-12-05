package org.example.steps;

import io.qameta.allure.Step;
import io.restassured.response.ValidatableResponse;
import org.example.model.User;
import static io.restassured.RestAssured.given;

public class UserSteps {
    public static final String USER = "/api/auth/register";
    public static final String LOGIN = "/api/auth/login/";
    public static final String DELETE = "/api/auth/user/";
    private String accessToken;

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String token) {
        this.accessToken = token;
    }

    @Step("Создание пользователя")
    public ValidatableResponse createUser(User user) {
        ValidatableResponse response = given()
                .body(user)
                .when()
                .post(USER)
                .then();
        this.accessToken = response.extract().path("accessToken");
        return response;
    }
    @Step("Логин пользователя")
    public ValidatableResponse loginUser(User user) {
        ValidatableResponse response = given()
                .body(user)
                .when()
                .post(LOGIN)
                .then();
        this.accessToken = response.extract().path("accessToken");
        return response;
    }
    @Step("Удаление пользователя")
    public ValidatableResponse deleteUser(String accessToken) {
        return given()
                .header("Authorization", accessToken)
                .when()
                .delete(DELETE)
                .then();
    }
}

