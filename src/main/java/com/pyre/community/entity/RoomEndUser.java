package com.pyre.community.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.pyre.community.enumeration.RoomRole;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.GenericGenerator;

import java.util.UUID;


@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class RoomEndUser extends BaseEntity {
    @Id
    @GeneratedValue(generator = "uuid2")
    @GenericGenerator(name = "uuid2", strategy = "uuid2")
    @Column(name = "ROOM_ENDUSER_ID", columnDefinition = "BINARY(16)")
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
    @Column(name = "USER_ID")
    private UUID userId;

    private RoomRole role;
    private Boolean owner;
    private int indexing;
    @Builder
    public RoomEndUser(
            Room room,
            UUID userId,
            RoomRole role,
            Boolean owner,
            int indexing
    ) {
        this.room = room;
        this.userId = userId;
        this.role = role;
        this.owner = owner;
        this.indexing = indexing;
    }
    public String getChannelTitle() {
        return this.channel.getTitle();
    }

}
