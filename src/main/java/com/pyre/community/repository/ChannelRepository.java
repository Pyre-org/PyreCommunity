package com.pyre.community.repository;


import com.pyre.community.entity.Channel;
import com.pyre.community.enumeration.ApprovalStatus;
import com.pyre.community.enumeration.ChannelGenre;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface ChannelRepository extends JpaRepository<Channel, UUID> {
    Page<Channel> findAllByGenreAndApprovalStatusAndTitleContaining(ChannelGenre Genre, ApprovalStatus approvalStatus, String title, Pageable pageable);
    Page<Channel> findAll(Pageable pageable);
    Page<Channel> findAllByApprovalStatus(ApprovalStatus approvalStatus, Pageable pageable);
    Page<Channel> findAllByApprovalStatusAndTitleContaining(ApprovalStatus approvalStatus, String title, Pageable pageable);
}
