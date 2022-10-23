package org.fatmansoft.teach.repository;

import org.fatmansoft.teach.models.Honor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface HonorRepository extends JpaRepository<Honor, Integer> {
    List<Honor> findByHonorName(String honorName);

    @Query(value = "select max(id) from Honor ")
    Integer getMaxId();

    @Query(value = "from Honor where ?1='' or honorName like %?1% ")
    List<Honor> findHonorListByNumName(String numName);

    @Query(value = "select * from honor where ?1 = '' or honor_name like %?1% ", nativeQuery = true)
    List<Honor> findHonorListByNumNameNative(String numName);
}