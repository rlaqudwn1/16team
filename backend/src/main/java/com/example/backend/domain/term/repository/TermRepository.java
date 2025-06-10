package com.example.backend.domain.term.repository;

import com.example.backend.domain.term.entity.Term;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface TermRepository extends JpaRepository<Term, Integer> {

    @Query("""
    SELECT DISTINCT t FROM Term t 
    LEFT JOIN t.relatedTerms r 
    WHERE LOWER(t.term) LIKE LOWER(CONCAT('%', :keyword, '%')) 
       OR LOWER(r) LIKE LOWER(CONCAT('%', :keyword, '%'))
""")
    List<Term> searchByTermOrRelatedTerms(@Param("keyword") String keyword);
    boolean existsByTerm(String term);

    @Query(value = "SELECT * FROM term ORDER BY RAND() LIMIT 10", nativeQuery = true)
    List<Term> findRandomTerms();

}
