package com.pyre.community.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.pyre.community.enumeration.ChannelRole;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.Objects;

@Getter
@Entity
@NoArgsConstructor
public class ChannelEndUser {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "CHANNEL_ENDUSER_ID")
    private long id;
    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnore
    @ToString.Exclude
    @JoinColumn(name = "CHANNEL_ID")
    private Channel channel;

    @Column(name = "USER_ID")
    private Long userId;

    private ChannelRole role;

    private Boolean agreement;
    private LocalDateTime joinDate;
    private int indexing;
    private Boolean ban;
    @Builder
    public ChannelEndUser(
            Channel channel,
            Long userId,
            Boolean agreement,
            int indexing
    ) {

        this.channel = channel;
        this.userId = userId;
        this.agreement = agreement;
        this.role = ChannelRole.CHANNEL_USER;
        this.indexing = indexing;
        this.joinDate = LocalDateTime.now();
        this.ban = false;
    }
    public void updateIndexing(int indexing) {
        this.indexing = indexing;
    }
    public void updateBan(Boolean ban) {
        this.ban = ban;
    }
}
