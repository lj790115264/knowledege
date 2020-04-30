package com.brain.java.dao.repository;

import com.brain.java.dao.Note;
import org.springframework.data.repository.CrudRepository;

/**
 * @program: java
 * @description:
 * @author: lanj
 * @create: 2020-04-26 14:46
 */
public interface NoteRepository  extends CrudRepository<Note, Long> {
}
