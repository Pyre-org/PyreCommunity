package com.pyre.community.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.pyre.community.enumeration.RoomRole;
import jakarta.persistence.*;
import lombok.*;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.annotations.GenericGenerator;

import java.util.Objects;
import java.util.UUID;


@Getter
@Entity
@Slf4j
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class RoomEndUser extends BaseEntity {
    @Id
    @GeneratedValue(generator = "uuid2")
    @GenericGenerator(name = "uuid2", strategy = "uuid2")
    @Column(name = "ROOM_END_USER_ID", columnDefinition = "BINARY(16)")
    private UUID id;
    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnore
    @ToString.Exclude
    @JoinColumn(name = "ROOM_ID")
    private Room room;
    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnore
    @ToString.Exclude
    @JoinColumn(name = "CHANNEL_ID")
    private Channel channel;
    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnore
    @ToString.Exclude
    @JoinColumn(name = "CHANNEL_END_USER_ID")
    private ChannelEndUser channelEndUser;
    @Column(name = "USER_ID")
    private UUID userId;
    @Enumerated(value = EnumType.STRING)
    private RoomRole role;
    private Boolean owner;
    private UUID prevId;
    private UUID nextId;
    private Boolean isDeleted;
    @Builder
    public RoomEndUser(
            Room room,
            UUID userId,
            RoomRole role,
            Boolean owner,
            UUID prevId,
            Channel channel,
            ChannelEndUser channelEndUser
    ) {
        this.room = room;
        this.userId = userId;
        this.role = role;
        this.owner = owner;
        this.prevId = prevId;
        this.nextId = null;
        this.channel = channel;
        this.isDeleted = false;
        this.channelEndUser = channelEndUser;
    }
    public void updateNext(UUID nextId) {
        this.nextId = nextId;
    }
    public void updatePrev(UUID prevId) {
        this.prevId = prevId;
    }
    public void updateRole(RoomRole role) {
        this.role = role;
    }
    public void updateIsDeleted(Boolean isDeleted) {
        this.isDeleted = isDeleted;
    }
}
