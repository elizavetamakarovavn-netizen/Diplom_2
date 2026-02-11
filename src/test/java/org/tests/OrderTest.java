package org.tests;

import io.qameta.allure.Description;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.ValidatableResponse;
import org.apache.commons.lang3.RandomStringUtils;
import org.example.model.User;
import org.example.steps.IngredientSteps;
import org.example.steps.OrderSteps;
import org.example.steps.UserSteps;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import java.util.List;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.assertEquals;
import static org.apache.http.HttpStatus.*;

public class OrderTest extends BaseTest {

    private UserSteps userSteps;
    private OrderSteps orderSteps;
    private IngredientSteps ingredientSteps;
    private User user;

    @Before
    public void setUp() {
        userSteps = new UserSteps();
        orderSteps = new OrderSteps();
        ingredientSteps = new IngredientSteps();
        user = new User()
                .setEmail(RandomStringUtils.randomAlphabetic(10).toLowerCase() + "@mail.com")
                .setPassword(RandomStringUtils.randomAlphabetic(10))
                .setName(RandomStringUtils.randomAlphabetic(10));
        ValidatableResponse registerResponse = userSteps.createUser(user);
        int statusCode = registerResponse.extract().statusCode();
        assertEquals(SC_OK, statusCode);
        orderSteps.setAccessToken(userSteps.getAccessToken());
    }


    @Test
    @DisplayName("Создание заказа из нескольких ингредиентов под авторизованным пользователем")
    @Description("Проверяет успешное создание заказа авторизованным пользователем с использованием нескольких случайных ингредиентов")
    public void createOrderWithAuthMultipleIngredients() {
        List<String> ingredientIds = ingredientSteps.getRandomIngredientIds(3);
        ValidatableResponse response = orderSteps.createOrderWithAuth(ingredientIds);

        assertThat(response.extract().statusCode(), equalTo(SC_OK));
        assertThat(response.extract().path("success"), equalTo(true));
        assertThat(response.extract().path("order.number"), notNullValue());
    }


    @Test
    @DisplayName("Создание заказа из одного ингредиента под авторизованным пользователем")
    @Description("Проверяет успешное создание заказа авторизованным пользователем с одним случайным ингредиентом")
    public void createOrderWithAuthSingleIngredient() {
        List<String> ingredientIds = ingredientSteps.getRandomIngredientIds(1);
        ValidatableResponse response = orderSteps.createOrderWithAuth(ingredientIds);

        assertThat(response.extract().statusCode(), equalTo(SC_OK));
        assertThat(response.extract().path("success"), equalTo(true));
        assertThat(response.extract().path("order.number"), notNullValue());
    }


    @Test
    @DisplayName("Создание заказа из нескольких ингредиентов под неавторизованным пользователем")
    @Description("Проверяет создание заказа без авторизации — заказ должен успешно создаваться даже без токена")
    public void createOrderWithoutAuth() {
        List<String> ingredientIds = ingredientSteps.getRandomIngredientIds(2);
        ValidatableResponse response = orderSteps.createOrderWithoutAuth(ingredientIds);

        assertThat(response.extract().statusCode(), equalTo(SC_OK));
        assertThat(response.extract().path("success"), equalTo(true));
        assertThat(response.extract().path("order.number"), notNullValue());
    }

    @Test
    @DisplayName("Создание заказа без ингредиентов под авторизованным пользователем")
    @Description("Проверяет, что заказ не создаётся, если авторизованный пользователь не передал ингредиенты. Ожидается ошибка 400.")
    public void createOrderNoIngredientsWithAuth() {
        ValidatableResponse response = orderSteps.createOrderWithoutIngredients(true);

        assertThat(response.extract().statusCode(), equalTo(SC_BAD_REQUEST));
        assertThat(response.extract().path("message"),
                equalTo("Ingredient ids must be provided"));
    }

    @Test
    @DisplayName("Создание заказа без ингредиентов под неавторизованным пользователем")
    @Description("Проверяет, что заказ не создаётся без ингредиентов при отсутствии авторизации. Ожидается ошибка 400.")
    public void createOrderNoIngredientsWithoutAuth() {
        ValidatableResponse response = orderSteps.createOrderWithoutIngredients(false);

        assertThat(response.extract().statusCode(), equalTo(SC_BAD_REQUEST));
        assertThat(response.extract().path("message"),
                equalTo("Ingredient ids must be provided"));
    }

    @Test
    @DisplayName("Создание заказа с неверными хешами ингредиентов под авторизованным пользователем")
    @Description("Проверяет поведение системы при передаче некорректных хешей ингредиентов. Ожидается ошибка 500.")
    public void createOrderWithInvalidIngredients() {
        List<String> invalidIds = List.of("61c0c5a71d1f82001bdaaa6d3", "61c0c5a71d1f82001bdaaa704", "61c0c5a71d1f82001bdaaa727");
        ValidatableResponse response = orderSteps.createOrderWithInvalidIngredients(invalidIds, true);
        assertThat(response.extract().statusCode(), equalTo(SC_INTERNAL_SERVER_ERROR));
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



