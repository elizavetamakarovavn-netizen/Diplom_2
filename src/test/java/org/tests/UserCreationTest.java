package org.tests;

import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.ValidatableResponse;
import org.apache.commons.lang3.RandomStringUtils;
import org.example.model.User;
import org.example.steps.UserSteps;
import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;

public class UserCreationTest extends BaseTest {

    private UserSteps userSteps;
    private User user;

    @Before
    public void setUp() {
        userSteps = new UserSteps();
        user = new User();
        user.setEmail(RandomStringUtils.randomAlphabetic(10).toLowerCase() + "@mail.com")
                .setPassword(RandomStringUtils.randomAlphabetic(10))
                .setName(RandomStringUtils.randomAlphabetic(10));
    }


    @Test
    @DisplayName("Создание уникального пользователя")
    public void createUniqueUserTest() {
        ValidatableResponse response = userSteps.createUser(user);
        int statusCode = response.extract().statusCode();

        assertThat("Статус код должен быть 200", statusCode, equalTo(200));
        assertThat(response.extract().path("success"), equalTo(true));
        assertThat(response.extract().path("user.email"), equalTo(user.getEmail()));
        assertThat(response.extract().path("user.name"), equalTo(user.getName()));
        String accessToken = response.extract().path("accessToken");
        assertThat(accessToken.startsWith("Bearer "), equalTo(true));

        userSteps.setAccessToken(accessToken);
    }


    @Test
    @DisplayName("Создание уже зарегистрированного пользователя")
    public void createExistingUserTest() {
        userSteps.createUser(user);
        ValidatableResponse response = userSteps.createUser(user);

        int statusCode = response.extract().statusCode();
        assertThat(statusCode, equalTo(403));
        assertThat(response.extract().path("success"), equalTo(false));
        assertThat(response.extract().path("message"), equalTo("User already exists"));
    }


    @Test
    @DisplayName("Создание пользователя с пропущенным паролем")
    public void createUserMissingPasswordTest() {
        User userMissingField = new User();
        userMissingField.setEmail(RandomStringUtils.randomAlphabetic(10) + "@mail.com")
                .setName(RandomStringUtils.randomAlphabetic(10));
        ValidatableResponse response = userSteps.createUser(userMissingField);

        int statusCode = response.extract().statusCode();
        assertThat(statusCode, equalTo(403));
        assertThat(response.extract().path("success"), equalTo(false));
        assertThat(response.extract().path("message"), equalTo("Email, password and name are required fields"));
    }

    @Test
    @DisplayName("Создание пользователя с пропущенным email")
    public void createUserMissingEmailTest() {
        User userMissingField = new User();
        userMissingField.setPassword(RandomStringUtils.randomAlphabetic(10) + "@mail.com")
                .setName(RandomStringUtils.randomAlphabetic(10));
        ValidatableResponse response = userSteps.createUser(userMissingField);

        int statusCode = response.extract().statusCode();
        assertThat(statusCode, equalTo(403));
        assertThat(response.extract().path("success"), equalTo(false));
        assertThat(response.extract().path("message"), equalTo("Email, password and name are required fields"));
    }

    @Test
    @DisplayName("Создание пользователя с пропущенным именем")
    public void createUserMissingNameTest() {
        User userMissingField = new User();
        userMissingField.setEmail(RandomStringUtils.randomAlphabetic(10) + "@mail.com")
                .setPassword(RandomStringUtils.randomAlphabetic(10));
        ValidatableResponse response = userSteps.createUser(userMissingField);

        int statusCode = response.extract().statusCode();
        assertThat(statusCode, equalTo(403));
        assertThat(response.extract().path("success"), equalTo(false));
        assertThat(response.extract().path("message"), equalTo("Email, password and name are required fields"));
    }
}

