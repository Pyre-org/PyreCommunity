package com.pyre.community.repository;

import com.pyre.community.entity.Channel;
import com.pyre.community.entity.Room;
import com.pyre.community.enumeration.RoomType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface RoomRepository extends JpaRepository<Room, UUID> {
    List<Room> findAllByChannelAndTypeAndTitleStartingWithOrderByTitle(Channel channel, RoomType type, String title);
    Room findByChannelAndType(Channel channel, RoomType type);
    Boolean existsByChannelAndTitle(Channel channel, String title);
}
