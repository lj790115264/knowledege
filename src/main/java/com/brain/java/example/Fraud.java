package com.brain.java.example;

// javac -cp neo4j-2.txt-driver*.jar:. Fraud.2.txt
// 2.txt -cp neo4j-2.txt-driver*.jar:. Fraud

import org.neo4j.driver.v1.*;
import org.neo4j.driver.v1.types.Path;

import static org.neo4j.driver.v1.Values.parameters;

import java.util.List;
import static java.util.Arrays.asList;
import static java.util.Collections.singletonMap;

public class Fraud {

    public static void main(String...args) {

        Config noSSL = Config.build().withEncryptionLevel(Config.EncryptionLevel.NONE).toConfig();
        Driver driver = GraphDatabase.driver("bolt://localhost",AuthTokens.basic("neo4j","loveyou55!"),noSSL); // <password>
        try (Session session = driver.session()) {

//            String insertQuery =
//                    "CREATE (hank:Person {name:'Hank'})," +
//                            "(abby:Person {name:'Abby'})," +
//                            "(max:Person {name:'Max'})," +
//                            "(sophie:Person {name: 'Sophie'})," +
//                            "(jane:Person {name: 'Jane'})," +
//                            "(bill:Person {name: 'Bill'})," +
//                            "(ssn993632634:SSN {number: 993632634})," +
//                            "(ssn123456789:SSN {number: 123456789})," +
//                            "(ssn523252364:SSN {number: 523252364})," +
//                            "(chase:Account {bank: 'Chase', number: 1523})," +
//                            "(bofa:Account {bank: 'Bank of America', number: 4634})," +
//                            "(cayman:Account {bank: 'Cayman', number: 863})," +
//                            "(bill)-[:HAS_SSN]->(ssn523252364)," +
//                            "(bill)-[:HAS_ACCOUNT]->(bofa)," +
//                            "(jane)-[:HAS_SSN]->(ssn123456789)," +
//                            "(jane)-[:HAS_ACCOUNT]->(chase)," +
//                            "(hank)-[:HAS_ACCOUNT]->(cayman)," +
//                            "(abby)-[:HAS_ACCOUNT]->(cayman)," +
//                            "(abby)-[:HAS_SSN]->(ssn993632634)," +
//                            "(sophie)-[:HAS_SSN]->(ssn993632634)," +
//                            "(max)-[:HAS_SSN]->(ssn993632634)";
//
//            session.run(insertQuery,parameters()).consume();

            StatementResult result;
//
//            String transitiveQuery =
//                    " MATCH (n:Person)-[*]-(o) " +
//                            " WHERE n.name = {name} "+
//                            " RETURN o ";
//
//            result = session.run(transitiveQuery, parameters("name","Hank"));
//            while (result.hasNext()) System.out.println(result.next().get("o").asMap());
//
//            String targetingQuery =
//                    "MATCH (n:Person)-[*]-(o) " +
//                            " WITH n, count(DISTINCT o) AS size " +
//                            " WHERE size > 2.txt " +
//                            " RETURN n";
//
//            result = session.run(targetingQuery, parameters());
//            while (result.hasNext()) System.out.println(result.next().get("n").asMap());

            String insightsQuery =
                    "MATCH p=(n:Service)-[r]-()  RETURN p";
            result = session.run(insightsQuery);
            while (result.hasNext()) {
                Record next = result.next();
                Path p = next.get("p").asPath();
                System.out.println(p);
            }

        }
    }
}
