package com.codecollect.repository;

import com.codecollect.entity.Tag;
import com.codecollect.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Repository
public interface TagRepository extends JpaRepository<Tag, Long> {
    List<Tag> findByUserOrderByNameAsc(User user);
    Optional<Tag> findByIdAndUser(Long id, User user);
    Optional<Tag> findByNameAndUser(String name, User user);
    Set<Tag> findByNameInAndUser(List<String> names, User user);
    boolean existsByNameAndUser(String name, User user);
    
    @Query("SELECT t FROM Tag t JOIN t.snippets s WHERE s.id = :snippetId")
    List<Tag> findBySnippetId(@Param("snippetId") Long snippetId);
}