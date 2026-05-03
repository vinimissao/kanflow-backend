package com.kanflow.repository;

import com.kanflow.domain.entity.Sprint;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface SprintRepository extends JpaRepository<Sprint, UUID> {
}
