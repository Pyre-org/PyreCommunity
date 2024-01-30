package com.pyre.community.repository;


import com.pyre.community.entity.Channel;
import com.pyre.community.entity.ChannelEndUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ChannelEndUserRepository extends JpaRepository<ChannelEndUser, Long> {
    Boolean existsByChannelAndUserId(Channel channel, long userId);
}
