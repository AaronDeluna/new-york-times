package com.javacademy.new_york_times.controller;

import com.javacademy.new_york_times.dto.NewsDto;
import com.javacademy.new_york_times.dto.PageNewsDto;
import com.javacademy.new_york_times.service.NewsService;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

/**
 * Сделать 7 операций внутри контроллера.
 * 1. Создание новости. Должно чистить кэш. - Готово
 * 2. Удаление новости по id. Должно чистить кэш. - Готово
 * 3. Получение новости по id. Должно быть закэшировано. - Готово
 * 4. Получение всех новостей (новости должны отдаваться порциями по 10 штук). Должно быть закэшировано. - Готово
 * 5. Обновление новости по id. Должно чистить кэш. - Подумать над ожидаемым значенеием
 * 6. Получение текста конкретной новости. - Готово
 * 7. Получение автора конкретной новости. - Готово
 */
@RestController
@RequestMapping("/news")
@RequiredArgsConstructor
public class NewsController {
    private final NewsService newsService;

    @GetMapping("/{id}")
    @Cacheable("news")
    public NewsDto getById(@PathVariable Integer id) {
        return newsService.findByNumber(id);
    }

    @GetMapping("/{id}/text")
    @Cacheable("newsText")
    public String getTextById(@PathVariable Integer id) {
        return newsService.getNewsText(id);
    }

    @GetMapping("/{id}/author")
    @Cacheable("newsAuthor")
    public String getAuthorById(@PathVariable Integer id) {
        return newsService.getNewsAuthor(id);
    }

    @GetMapping
    @Cacheable("news")
    public PageNewsDto getAllNews(@RequestParam Integer page) {
        return newsService.findAll(page);
    }

    @PostMapping
    @CacheEvict(value = "news", allEntries = true)
    @ResponseStatus(HttpStatus.CREATED)
    public void createNews(@RequestBody NewsDto news) {
        newsService.save(news);
    }

    @PutMapping
    @CacheEvict("news")
    public void updateNewsById(@RequestBody NewsDto newsDto) {
        newsService.update(newsDto);
    }

    @DeleteMapping("/{id}")
    @CacheEvict("news")
    public boolean deleteById(@PathVariable Integer id) {
        return newsService.deleteByNumber(id);
    }

}
