package com.pyre.community.repository;

import com.pyre.community.entity.Room;
import com.pyre.community.entity.Space;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface SpaceRepository extends JpaRepository<Space, UUID> {
    List<Space> findAllByRoomAndIsDeleted(Room room, Boolean isDeleted);
}
