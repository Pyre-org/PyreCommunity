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
    List<RoomEndUser> findAllByChannelAndUserIdAndIsDeleted(Channel channel, UUID userId, Boolean isDeleted);
    List<RoomEndUser> findAllByRoomAndIsDeleted(Room room, Boolean isDeleted);
    List<RoomEndUser> findTop1ByUserId(UUID userId);

    List<RoomEndUser> findAllByUserIdAndIsDeleted(UUID userId, Boolean isDeleted);
    Boolean existsByRoomAndUserIdAndIsDeleted(Room room, UUID userId, Boolean isDeleted);
    Optional<RoomEndUser> findByRoomAndUserIdAndIsDeleted(Room room, UUID userId, Boolean isDeleted);
}
