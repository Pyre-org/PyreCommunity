package com.pyre.community.repository;

import com.pyre.community.entity.Channel;
import com.pyre.community.entity.Room;
import com.pyre.community.entity.RoomEndUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface RoomEndUserRepository extends JpaRepository<RoomEndUser, UUID> {
    List<RoomEndUser> findAllByChannelAndUserId(Channel channel, UUID userId);
    List<RoomEndUser> findAllByRoom(Room room);
    List<RoomEndUser> findTop1ByUserId(UUID userId);
    List<RoomEndUser> findAllByUserId(UUID userId);
    Boolean existsByRoomAndUserId(Room room, UUID userId);
    Optional<RoomEndUser> findByRoomAndUserId(Room room, UUID userId);
}
