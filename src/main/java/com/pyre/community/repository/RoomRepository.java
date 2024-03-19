package com.pyre.community.repository;

import com.pyre.community.entity.Channel;
import com.pyre.community.entity.Room;
import com.pyre.community.enumeration.RoomType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface RoomRepository extends JpaRepository<Room, UUID> {
    Page<Room> findAllByChannelAndTypeAndTitleContaining(Channel channel, RoomType type, String title, Pageable pageable);
    Room findByChannelAndType(Channel channel, RoomType type);
    Boolean existsByChannelAndTitle(Channel channel, String title);
    @Query("SELECT r FROM Room r WHERE (r.title LIKE %?1% AND r.type = ?2) OR r in (SELECT re.room FROM RoomEndUser re WHERE re.userId = ?3 AND re.isDeleted = false AND (re.room.title LIKE %?1%)) ")
    Page<Room> findAllByTitleSearch(String title, RoomType type, UUID userId, Pageable pageable);
}
