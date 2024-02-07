package com.pyre.community.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.pyre.community.enumeration.ChannelRole;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.GenericGenerator;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ChannelEndUser extends BaseEntity {
    @Id
    @GeneratedValue(generator = "uuid2")
    @GenericGenerator(name = "uuid2", strategy = "uuid2")
    @Column(name = "CHANNEL_ENDUSER_ID", columnDefinition = "BINARY(16)")
    private UUID id;
    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnore
    @ToString.Exclude
    @JoinColumn(name = "CHANNEL_ID")
    private Channel channel;

    @Column(name = "USER_ID")
    private UUID userId;

    private ChannelRole role;

    private Boolean agreement;
    private LocalDateTime joinDate;
    private int indexing;
    private Boolean ban;
    @Builder
    public ChannelEndUser(
            Channel channel,
            UUID userId,
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
