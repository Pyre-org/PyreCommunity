package com.pyre.community.repository;


import com.pyre.community.entity.Channel;
import com.pyre.community.entity.ChannelEndUser;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ChannelEndUserRepository extends JpaRepository<ChannelEndUser, UUID> {
    Boolean existsByChannelAndUserId(Channel channel, UUID userId);
    Optional<ChannelEndUser> findByChannelAndUserId(Channel channel, UUID userId);
    List<ChannelEndUser> findAllByUserId(UUID userId, Sort sort);
    List<ChannelEndUser> findAllByUserId(UUID userId);
    List<ChannelEndUser> findTop1ByUserIdOrderByIndexingDesc(UUID userId);
}
