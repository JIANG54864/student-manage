package org.fatmansoft.teach.repository;

import org.fatmansoft.teach.models.Dept;
import org.fatmansoft.teach.models.Province;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

@Repository
public interface ProvinceRepository extends JpaRepository<Province,Integer> {
}
