package com.pyre.community.repository;

import com.pyre.community.entity.RoomEndUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RoomEndUserRepository extends JpaRepository<RoomEndUser, Long> {
}
