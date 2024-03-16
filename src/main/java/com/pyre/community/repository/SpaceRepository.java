package com.pyre.community.repository;

import com.pyre.community.entity.Room;
import com.pyre.community.entity.Space;
import com.pyre.community.enumeration.RoomRole;
import com.pyre.community.enumeration.RoomType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface SpaceRepository extends JpaRepository<Space, UUID> {
    List<Space> findAllByRoomAndIsDeleted(Room room, Boolean isDeleted);
    @Query("SELECT s FROM Space s WHERE (s.title LIKE %?1% AND s.isDeleted = false AND s.room.type = ?2 AND s.role = 'SPACEROLE_GUEST')")
    Page<Space> findAllByTitleSearch(String title, RoomType type, Pageable pageable);
}
