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

@Repository
public interface ChannelEndUserRepository extends JpaRepository<ChannelEndUser, Long> {
    Boolean existsByChannelAndUserId(Channel channel, Long userId);
    Optional<ChannelEndUser> findByChannelAndUserId(Channel channel, Long userId);
    List<ChannelEndUser> findAllByUserId(Long userId, Sort sort);
    List<ChannelEndUser> findAllByUserId(Long userId);
    List<ChannelEndUser> findTop1ByUserIdOrderByIndexingDesc(Long userId);
}
