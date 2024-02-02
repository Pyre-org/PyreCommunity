package com.pyre.community.repository;


import com.pyre.community.entity.Channel;
import com.pyre.community.enumeration.ApprovalStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ChannelRepository extends JpaRepository<Channel, Long> {
    Page<Channel> findAllByGenreAndApprovalStatusAndTitleStartingWith(String Genre, ApprovalStatus approvalStatus, String title, Pageable pageable);
    Page<Channel> findAll(Pageable pageable);
    Page<Channel> findAllByApprovalStatus(ApprovalStatus approvalStatus, Pageable pageable);
    Page<Channel> findAllByApprovalStatusAndTitleStartingWith(ApprovalStatus approvalStatus, String title, Pageable pageable);
}
