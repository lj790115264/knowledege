package com.brain.java.dao.repository;

import com.brain.java.dao.Article;
import org.springframework.data.repository.CrudRepository;

/**
 * @program: java
 * @description:
 * @author: liyx
 * @create: 2020-04-22 15:34
 */
public interface ArticleRepository extends CrudRepository<Article, Long> {

}
