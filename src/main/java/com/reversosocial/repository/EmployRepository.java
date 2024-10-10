package com.reversosocial.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.reversosocial.models.entity.Employ;

@Repository
public interface EmployRepository extends JpaRepository<Employ, Integer> {

}
