package org.fatmansoft.teach.repository;

import org.fatmansoft.teach.models.Journal;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface JournalRepository extends JpaRepository<Journal,Integer> {
    @Query(value = "select max(id) from Course  ")
    Integer getMaxId();

    @Query(value = "select * from journal  where ?1='' or leave_Record like %?1% or consume_Record like %?1% ", nativeQuery = true)
    List<Journal> findJournalListByNumNameNative(String numName);

}
