package com.brain.java.controller;

import com.brain.java.dao.KNode;
import com.brain.java.dao.Note;
import com.brain.java.dao.Relation;
import com.brain.java.dao.repository.KNodeRepository;
import com.brain.java.dao.repository.NoteRepository;
import com.brain.java.dao.repository.RelationRepository;
import com.brain.java.dto.request.*;
import com.brain.java.dto.response.QueryRelationNode;
import com.brain.java.dto.response.QueryRelationResponse;
import org.elasticsearch.index.query.QueryBuilders;
import org.neo4j.driver.v1.Driver;
import org.neo4j.driver.v1.Record;
import org.neo4j.driver.v1.Session;
import org.neo4j.driver.v1.StatementResult;
import org.neo4j.driver.v1.types.Node;
import org.neo4j.driver.v1.types.Path;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.data.elasticsearch.core.query.SearchQuery;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@CrossOrigin
@RequestMapping("node")
public class KNodeController {

    @Autowired
    private KNodeRepository kNodeRepository;

    @Autowired
    private RelationRepository relationRepository;

    @Autowired
    private NoteRepository noteRepository;

    @Autowired
    private ElasticsearchOperations elasticsearchOperations;

    @Autowired
    private Driver driver;

    @PostMapping("add")
    public Object addNode(@RequestBody AddNodeRequest request) {

        String name = request.getName();
        try (Session session = driver.session()) {
            String insertQuery = "merge (n:node:" + name + " {name:'" + name + "'}) return n";
            StatementResult run = session.run(insertQuery);
            List<Record> list = run.list();
            Long id = list.get(0).get("n").asNode().id();
            KNode node = new KNode();
            node.setName(name);
            node.setId(id);
            node.setRemark(request.getRemark());
            node.setType("node");
            kNodeRepository.save(node);
            return id;
        }
    }

    @PostMapping("add/relation")
    public Object addRelation(@RequestBody AddRelationRequest request) {

        Long relationNodeId = request.getRelationId();
        String relation = request.getRelation();
        if (null == relationNodeId) {
            try (Session session = driver.session()) {
                String properties;
                if (null != request.getRelationRemark() && !"".equals(request.getRelationRemark())) {
                    properties = String.format("{name:'%s', remark:'%s'}", relation, request.getRelationRemark());
                } else {
                    properties = String.format("{name:'%s'}", relation);
                }
                String insertQuery = "merge (r:relation:" + relation + " " + properties + ") return r";
                StatementResult run = session.run(insertQuery);
                List<Record> list = run.list();
                Long id = list.get(0).get("r").asNode().id();
                KNode node = new KNode();
                node.setName(relation);
                node.setRemark(request.getRelationRemark());
                node.setId(id);
                node.setType("relationNode");
                kNodeRepository.save(node);
                relationNodeId = id;
            }

            try (Session session = driver.session()) {
                String insertQuery = String.format("match (r), (n)  where id(r)=%s and id(n)=%s " +
                        "merge p=(r)-[k:%s]->(n) return p", request.getId(), relationNodeId, relation);
                StatementResult run = session.run(insertQuery);
                List<Record> list = run.list();
                Long relationId = list.get(0).get("p").asPath().relationships().iterator().next().id();
                Relation r = new Relation();
                r.setName(relation);
                r.setId(relationId);
                r.setType("relation");
                relationRepository.save(r);
            }
        }

        for (Long child : request.getChild()) {
            try (Session session = driver.session()) {
                String insertQuery = String.format("match (r), (n)  where id(r)=%s and id(n)=%s " +
                        "merge p=(r)-[k:%s]->(n) return p", relationNodeId, child, relation);
                StatementResult run = session.run(insertQuery);
                List<Record> list = run.list();
                Long relationId = list.get(0).get("p").asPath().relationships().iterator().next().id();
                Relation r = new Relation();
                r.setName(relation);
                r.setId(relationId);
                r.setType("relation");
                relationRepository.save(r);
            }
        }

        return relationNodeId;
    }

    @PostMapping("list")
    public Object list(@RequestBody QueryNodeRequest request) {

        SearchQuery searchQuery = new NativeSearchQueryBuilder()
                .withQuery(QueryBuilders.queryStringQuery(request.getContent()))
                .withPageable(PageRequest.of(0, 10))
                .build();
        List<KNode> nodes = elasticsearchOperations.queryForList(searchQuery, KNode.class);
        return nodes;
    }

    @PostMapping("relations")
    public Object relations(@RequestBody QueryRelationRequest request) {

        String query = String.format("match (r) where id(r)=%s match p=(r)-[*2]->() return p", request.getId());

        Map<Long, QueryRelationResponse> responseMap = new HashMap<>();

        try (Session session = driver.session()) {
            StatementResult run = session.run(query);
            List<Record> list = run.list();
            for (Record record : list) {
                Iterator<Path.Segment> p = record.get("p").asPath().iterator();

                // 第一个节点代表 关系节点
                Path.Segment relationNodeSegment = p.next();
                Node end = relationNodeSegment.end();
                long id = end.id();
                QueryRelationResponse response = responseMap.get(id);
                if (null == response) {
                    Map<String, Object> map1 = end.asMap();
                    response = QueryRelationResponse.builder()
                            .relationId(id)
                            .relation((String) map1.get("name"))
                            .relationRemark((String) map1.get("remark"))
                            .list(new ArrayList<>())
                            .build();
                    responseMap.put(id, response);
                }
                // 后面的是关系的子关系节点
                while (p.hasNext()) {
                    Path.Segment next = p.next();
                    // 子关系节点的关系id
                    long subRelationId = next.relationship().id();
                    // 子关系节点的节点id
                    long subNodeId = next.end().id();
                    String subNodeName = (String) next.end().asMap().get("name");
                    QueryRelationNode node =
                            QueryRelationNode.builder().relationId(subRelationId).nodeId(subNodeId).nodeName(subNodeName).build();
                    response.getList().add(node);
                }
            }
        }

        return responseMap.values();
    }

    @PostMapping("note/relation")
    public Object notes(@RequestBody QueryNoteRequest request) {

        SearchQuery searchQuery = new NativeSearchQueryBuilder()
                .withQuery(QueryBuilders.boolQuery()
                        .must(QueryBuilders.termQuery("nodeId", request.getId()))
                        .must(QueryBuilders.termQuery("type", request.getType()))
                )
                .withPageable(PageRequest.of(0, 10))
                .build();
        List<Note> nodes = elasticsearchOperations.queryForList(searchQuery, Note.class);
        return nodes;
    }

    @PostMapping("note/relation/add")
    public Object addNote(@RequestBody NoteRelationRequest request) {

        Note note = new Note();
        note.setType(0);
        note.setNodeId(request.getId());
        note.setContent(request.getContent());
        noteRepository.save(note);

        return note.getId();
    }
}
