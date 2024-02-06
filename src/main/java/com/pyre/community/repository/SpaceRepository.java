package com.pyre.community.repository;

import com.pyre.community.entity.Space;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface SpaceRepository extends JpaRepository<Space, UUID> {
}