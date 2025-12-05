package org.tests;

import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.ValidatableResponse;
import org.apache.commons.lang3.RandomStringUtils;
import org.example.model.User;
import org.example.steps.UserSteps;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertEquals;

public class UserLoginTest extends BaseTest {

    private UserSteps userSteps;
    private User user;

    @Before
    public void setUp() {
        userSteps = new UserSteps();
        user = new User()
                .setEmail(RandomStringUtils.randomAlphabetic(10).toLowerCase() + "@mail.com")
                .setPassword(RandomStringUtils.randomAlphabetic(10))
                .setName(RandomStringUtils.randomAlphabetic(10));
        ValidatableResponse registerResponse = userSteps.createUser(user);
        int statusCode = registerResponse.extract().statusCode();
        assertEquals(200, statusCode);
        userSteps.setAccessToken(userSteps.getAccessToken());
    }

    @Test
    @DisplayName("Успешная авторизация")
    public void loginSuccess() {
        ValidatableResponse registerResponse = userSteps.createUser(user);
        userSteps.setAccessToken(registerResponse.extract().path("accessToken"));
        ValidatableResponse loginResponse = userSteps.loginUser(user);
        loginResponse
                .statusCode(200)
                .body("success", equalTo(true))
                .body("user.email", equalTo(user.getEmail()))
                .body("user.name", equalTo(user.getName()));
    }

    @Test
    @DisplayName("Авторизация с неверным email")
    public void loginWithIncorrectEmail() {
        ValidatableResponse registerResponse = userSteps.createUser(user);
        userSteps.setAccessToken(registerResponse.extract().path("accessToken"));
        String wrongEmail = "x" + user.getEmail().substring(1);
        User wrongEmailUser = new User()
                .setEmail(wrongEmail)
                .setPassword(user.getPassword());
        ValidatableResponse response = userSteps.loginUser(wrongEmailUser);
        response
                .statusCode(401)
                .body("success", equalTo(false))
                .body("message", equalTo("email or password are incorrect"));
    }

    @Test
    @DisplayName("Авторизация с неверным паролем")
    public void loginWithIncorrectPassword() {
        ValidatableResponse registerResponse = userSteps.createUser(user);
        userSteps.setAccessToken(registerResponse.extract().path("accessToken"));
        User wrongPasswordUser = new User()
                .setEmail(user.getEmail())
                .setPassword("IncorrectPassword123");
        ValidatableResponse response = userSteps.loginUser(wrongPasswordUser);
        response
                .statusCode(401)
                .body("success", equalTo(false))
                .body("message", equalTo("email or password are incorrect"));
    }

    @After
    public void tearDown() {
        String token = userSteps.getAccessToken();
        if (token != null) {
            userSteps.deleteUser(token)
                    .statusCode(202);
        }
    }
}





