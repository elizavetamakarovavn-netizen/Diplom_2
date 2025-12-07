# Diplom_2 — Автотесты API Stellar Burgers
Проект содержит автоматизированные тесты для API сервиса Stellar Burgers.
Тесты написаны на Java 11 с использованием JUnit 4, Rest-Assured, Maven и Allure Report.

| Технология       | Версия |
| ---------------- | ------ |
| Java             | 11     |
| Maven            | 3.9.0+ |
| JUnit            | 4.13.2 |
| Rest-Assured     | 5.5.6  |
| Jackson Databind | 2.20.1 |
| Allure           | 2.15.0 |
| AspectJ Weaver   | 1.9.7  |


**Тесты запускаются командой:** mvn clean test

**Тесты покрывают:**

 - Авторизация

 - Создание пользователя

 - Авторизация с неверными данными

 - Создание заказов

 - Проверка ответов API

**Allure-отчёт запускается командой:** allure serve target/allure-results