package com.brain.java.controller;

import com.brain.java.dao.Article;
import com.brain.java.dao.ArticleKNode;
import com.brain.java.dao.repository.ArticleKNodeRepository;
import com.brain.java.dao.repository.ArticleRepository;
import com.brain.java.dto.request.AddArticleRequest;
import com.brain.java.dto.request.QueryArticleRequest;
import com.brain.java.dto.request.QueryNodeArticleRequest;
import com.brain.java.dto.request.RelateArticleRequest;
import com.brain.java.dto.response.QueryNodeArticleResponse;
import org.elasticsearch.index.query.QueryBuilders;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.data.elasticsearch.core.query.SearchQuery;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @program: java
 * @description:
 * @author: lanj
 * @create: 2020-04-22 11:36
 */
@RestController
@CrossOrigin
@RequestMapping("article")
public class ArticleController {

    @Autowired
    private ArticleRepository articleRepository;

    @Autowired
    private ArticleKNodeRepository articleKNodeRepository;

    @Autowired
    private ElasticsearchOperations elasticsearchOperations;

    @PostMapping("add")
    public Object add(@RequestBody AddArticleRequest request) {

        Article article = new Article();
        article.setTitle(request.getTitle());
        article.setUrl(request.getUrl());
        Article save = articleRepository.save(article);
        return save;
    }

    @PostMapping("list")
    public Object list(@RequestBody QueryArticleRequest request) {

        SearchQuery searchQuery = new NativeSearchQueryBuilder()
                .withQuery(QueryBuilders.queryStringQuery(request.getContent()))
                .withPageable(PageRequest.of(0, 10))
                .build();
        List<Article> articles = elasticsearchOperations.queryForList(searchQuery, Article.class);
        return articles;
    }

    /**
     * 将已有的文章关联到节点上
     *
     * @param request
     * @return
     */
    @PostMapping("relate")
    public Object add(@RequestBody RelateArticleRequest request) {

        ArticleKNode articleKNode = new ArticleKNode();
        articleKNode.setArticleId(request.getArticleId());
        articleKNode.setNodeId(request.getNodeId());
        articleKNode.setType(request.getType());

        ArticleKNode save = articleKNodeRepository.save(articleKNode);
        return save;
    }

    /**
     * 查询当前节点的文章
     * @return
     */
    @PostMapping("node/articles")
    public Object nodeArticle(@RequestBody QueryNodeArticleRequest request) {

        QueryNodeArticleResponse response = new QueryNodeArticleResponse();

        SearchQuery searchQuery = new NativeSearchQueryBuilder()
                .withQuery(QueryBuilders.termQuery("nodeId",  request.getId()))
                .build();

        List<ArticleKNode> articleKNodes = elasticsearchOperations.queryForList(searchQuery, ArticleKNode.class);
        if (!CollectionUtils.isEmpty(articleKNodes)) {
            List<String> articleIds = articleKNodes.stream().map(i -> i.getArticleId()).collect(Collectors.toList());
            String[] articleIdsArr = articleIds.toArray(new String[articleIds.size()]);
            searchQuery = new NativeSearchQueryBuilder()
                    .withQuery(QueryBuilders.termsQuery("_id", articleIdsArr))
                    .build();
            List<Article> articles = elasticsearchOperations.queryForList(searchQuery, Article.class);
            response.setArticles(articles);
        } else {
            response.setArticles(new ArrayList<>());
        }

        return response;
    }
}