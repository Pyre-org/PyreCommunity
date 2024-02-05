package com.pyre.community.repository;

import com.pyre.community.entity.Channel;
import com.pyre.community.entity.RoomEndUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface RoomEndUserRepository extends JpaRepository<RoomEndUser, UUID> {
    Boolean existsByIdAndAndUserId(UUID id, UUID userId);
    List<RoomEndUser> findAllByChannelAndUserId(Channel channel, UUID userId);
    List<RoomEndUser> findAllByChannelAndUserIdOrderByIndexingAsc(Channel channel, UUID userId);
    List<RoomEndUser> findTop1ByUserIdOrderByIndexingDesc(UUID userId);
}
