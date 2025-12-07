package org.tests;

import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.ValidatableResponse;
import org.apache.commons.lang3.RandomStringUtils;
import org.example.model.User;
import org.example.steps.UserSteps;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.apache.http.HttpStatus.*;
import io.qameta.allure.Description;

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
    @Description("Проверяет успешное создание нового пользователя со всеми валидными полями. Ожидается статус 200, success=true и корректный accessToken")
    public void createUniqueUserTest() {

        ValidatableResponse response = userSteps.createUser(user);
        int statusCode = response.extract().statusCode();

        String accessToken = response.extract().path("accessToken");
        userSteps.setAccessToken(accessToken);

        assertThat("Статус код должен быть 200", statusCode, equalTo(SC_OK));
        assertThat(response.extract().path("success"), equalTo(true));
        assertThat(response.extract().path("user.email"), equalTo(user.getEmail()));
        assertThat(response.extract().path("user.name"), equalTo(user.getName()));
        assertThat(accessToken.startsWith("Bearer "), equalTo(true));
    }


    @Test
    @DisplayName("Создание уже зарегистрированного пользователя")
    @Description("Проверяет невозможность повторной регистрации того же пользователя" + "Ожидается статус 403 и сообщение 'User already exists'")
    public void createExistingUserTest() {
        userSteps.createUser(user);
        ValidatableResponse response = userSteps.createUser(user);

        int statusCode = response.extract().statusCode();

        assertThat(statusCode, equalTo(SC_FORBIDDEN));
        assertThat(response.extract().path("success"), equalTo(false));
        assertThat(response.extract().path("message"), equalTo("User already exists"));
    }


    @Test
    @DisplayName("Создание пользователя с пропущенным паролем")
    @Description("Проверяет валидацию создания пользователя при отсутствии пароля" + "Ожидается статус 403 и сообщение об обязательности email, password и name")
    public void createUserMissingPasswordTest() {
        User userMissingField = new User();
        userMissingField.setEmail(RandomStringUtils.randomAlphabetic(10) + "@mail.com")
                .setName(RandomStringUtils.randomAlphabetic(10));
        ValidatableResponse response = userSteps.createUser(userMissingField);

        int statusCode = response.extract().statusCode();

        assertThat(statusCode, equalTo(SC_FORBIDDEN));
        assertThat(response.extract().path("success"), equalTo(false));
        assertThat(response.extract().path("message"), equalTo("Email, password and name are required fields"));
    }

    @Test
    @DisplayName("Создание пользователя с пропущенным email")
    @Description("Проверяет невозможность создания пользователя без email" + "Ожидается статус 403 и сообщение о необходимости всех обязательных полей")
    public void createUserMissingEmailTest() {
        User userMissingField = new User();
        userMissingField.setPassword(RandomStringUtils.randomAlphabetic(10) + "@mail.com")
                .setName(RandomStringUtils.randomAlphabetic(10));
        ValidatableResponse response = userSteps.createUser(userMissingField);

        int statusCode = response.extract().statusCode();

        assertThat(statusCode, equalTo(SC_FORBIDDEN));
        assertThat(response.extract().path("success"), equalTo(false));
        assertThat(response.extract().path("message"), equalTo("Email, password and name are required fields"));
    }

    @Test
    @DisplayName("Создание пользователя с пропущенным именем")
    @Description("Проверяет невозможность создания пользователя без имени" + "Ожидается статус 403 и сообщение о необходимости обязательных полей")
    public void createUserMissingNameTest() {
        User userMissingField = new User();
        userMissingField.setEmail(RandomStringUtils.randomAlphabetic(10) + "@mail.com")
                .setPassword(RandomStringUtils.randomAlphabetic(10));
        ValidatableResponse response = userSteps.createUser(userMissingField);

        int statusCode = response.extract().statusCode();

        assertThat(statusCode, equalTo(SC_FORBIDDEN));
        assertThat(response.extract().path("success"), equalTo(false));
        assertThat(response.extract().path("message"), equalTo("Email, password and name are required fields"));
    }

    @After
    public void tearDown() {
        String token = userSteps.getAccessToken();
        if (token != null) {
            userSteps.deleteUser(token)
                    .statusCode(SC_ACCEPTED);
        }
    }
}

