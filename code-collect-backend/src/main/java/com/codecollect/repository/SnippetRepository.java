package com.codecollect.repository;

import com.codecollect.entity.Snippet;
import com.codecollect.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface SnippetRepository extends JpaRepository<Snippet, Long> {
    List<Snippet> findByUserAndArchivedFalseOrderByCreatedAtDesc(User user);
    
    List<Snippet> findByUserAndStarredTrueAndArchivedFalseOrderByCreatedAtDesc(User user);
    
    List<Snippet> findByUserAndProgrammingLanguageAndArchivedFalseOrderByCreatedAtDesc(User user, String language);
    
    List<Snippet> findByUserAndCategoryIdAndArchivedFalseOrderByCreatedAtDesc(User user, Long categoryId);
    
    Optional<Snippet> findByIdAndUser(Long id, User user);
    
    @Query("SELECT DISTINCT s FROM Snippet s LEFT JOIN s.tags t " +
           "WHERE s.user = :user AND s.archived = false AND " +
           "(LOWER(s.title) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
           "OR LOWER(s.description) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
           "OR LOWER(s.code) LIKE LOWER(CONCAT('%', :keyword, '%')))")
    List<Snippet> searchByKeyword(@Param("user") User user, @Param("keyword") String keyword);
    
    @Query("SELECT DISTINCT s FROM Snippet s JOIN s.tags t " +
           "WHERE s.user = :user AND s.archived = false AND t.id IN :tagIds")
    List<Snippet> findByTagIds(@Param("user") User user, @Param("tagIds") List<Long> tagIds);
    
    @Query("SELECT DISTINCT s.programmingLanguage FROM Snippet s WHERE s.user = :user")
    List<String> findDistinctProgrammingLanguagesByUser(@Param("user") User user);
}