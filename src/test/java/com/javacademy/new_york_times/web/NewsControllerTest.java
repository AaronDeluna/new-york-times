package com.javacademy.new_york_times.web;

import com.javacademy.new_york_times.dto.NewsDto;
import com.javacademy.new_york_times.dto.PageNewsDto;
import com.javacademy.new_york_times.service.NewsService;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.builder.ResponseSpecBuilder;
import io.restassured.filter.log.LogDetail;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;
import io.restassured.specification.ResponseSpecification;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;

import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Slf4j
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@AutoConfigureMockMvc
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public class NewsControllerTest {
    private final RequestSpecification requestSpecification = new RequestSpecBuilder()
            .setBasePath("/news")
            .setContentType(ContentType.JSON)
            .log(LogDetail.ALL)
            .build();
    private final ResponseSpecification responseSpecification = new ResponseSpecBuilder()
            .log(LogDetail.ALL)
            .build();

    @Autowired
    private NewsService newsService;

    @Test
    @DisplayName("Успешное получение новости по id")
    public void getByIdSuccess() {
        NewsDto expectedNewsDto = NewsDto.builder()
                .number(1)
                .title("News #1")
                .text("Today is Groundhog Day #1")
                .author("Molodyko Yuri")
                .build();

        NewsDto resultNewsDto = given(requestSpecification)
                .get("%s".formatted(expectedNewsDto.getNumber()))
                .then()
                .spec(responseSpecification)
                .statusCode(200)
                .extract()
                .as(NewsDto.class);

        assertEquals(expectedNewsDto, resultNewsDto);
    }

    @Test
    @DisplayName("Успешное получение текста новости по id")
    public void getTextByIdSuccess() {
        int newsId = 1;
        String expectedText = "Today is Groundhog Day #1";

        String resultText = given(requestSpecification)
                .get("%s/text".formatted(newsId))
                .then()
                .spec(responseSpecification)
                .statusCode(200)
                .extract()
                .asString();

        assertEquals(expectedText, resultText);
    }

    @Test
    @DisplayName("Успешное получение автора новости по id")
    public void getAuthorByIdSuccess() {
        int newsId = 1;
        String expectedAuthor = "Molodyko Yuri";

        String resultAuthor = given(requestSpecification)
                .get("%s/author".formatted(newsId))
                .then()
                .spec(responseSpecification)
                .statusCode(200)
                .extract()
                .asString();

        assertEquals(expectedAuthor, resultAuthor);
    }

    @Test
    @DisplayName("Успешное получение всех новостей по номеру старицы")
    public void getAllNewsSuccess() {
        int page = 0;

        NewsDto expectedNewsDto = NewsDto.builder()
                .number(1)
                .title("News #1")
                .text("Today is Groundhog Day #1")
                .author("Molodyko Yuri")
                .build();

        PageNewsDto resultPageNewsDto = given()
                .get("news?page=%s".formatted(page))
                .then()
                .spec(responseSpecification)
                .statusCode(200)
                .extract()
                .as(PageNewsDto.class);

        NewsDto resultNewsDto = resultPageNewsDto.getContent().get(0);
        log.info(resultNewsDto.toString());
        assertEquals(expectedNewsDto, resultNewsDto);
    }

    @Test
    @DisplayName("Успешное создание новой новости")
    public void createNewsSuccess() {
        String title = "Test title";
        String text = "Test text";
        String author = "Test author";

        NewsDto createNewsDto = NewsDto.builder()
                .title(title)
                .text(text)
                .author(author)
                .build();

        NewsDto expectedNewsDto = NewsDto.builder()
                .number(1001)
                .title(title)
                .text(text)
                .author(author)
                .build();

        given(requestSpecification)
                .body(createNewsDto)
                .post()
                .then()
                .spec(responseSpecification)
                .statusCode(201);

        NewsDto resultNewsDto = newsService.findByNumber(expectedNewsDto.getNumber());
        assertEquals(expectedNewsDto, resultNewsDto);
    }

    @Test
    @DisplayName("Успешное обновление новости по id")
    public void updateNewsByIdSuccess() {
        NewsDto expectedNewsDto = NewsDto.builder()
                .number(1)
                .title("Test update")
                .text("Test update")
                .author("Test update")
                .build();

        NewsDto updateNewsDto = NewsDto.builder()
                .title("Test update")
                .text("Test update")
                .author("Test update")
                .build();

        given(requestSpecification)
                .body(updateNewsDto)
                .patch("%s".formatted(expectedNewsDto.getNumber()))
                .then()
                .spec(responseSpecification)
                .statusCode(200);

        NewsDto resultNewsDto = newsService.findByNumber(expectedNewsDto.getNumber());
        assertEquals(expectedNewsDto, resultNewsDto);
    }

    @Test
    @DisplayName("Успешное удаление новости по id")
    public void deleteByIdSuccess() {
        int newsId = 1;
        Boolean resultDeleteNews = given(requestSpecification)
                .delete("%s".formatted(newsId))
                .then()
                .spec(responseSpecification)
                .statusCode(200)
                .extract()
                .as(Boolean.class);

        assertTrue(resultDeleteNews);
    }
}
