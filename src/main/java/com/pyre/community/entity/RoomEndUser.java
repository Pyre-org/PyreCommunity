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
    @Enumerated(value = EnumType.STRING)
    private RoomRole role;
    private Boolean owner;
    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private RoomEndUser prev;
    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private RoomEndUser next;

    @Builder
    public RoomEndUser(
            Room room,
            UUID userId,
            RoomRole role,
            Boolean owner,
            RoomEndUser prev
    ) {
        this.room = room;
        this.userId = userId;
        this.role = role;
        this.owner = owner;
        this.prev = prev;
        this.next = null;
    }
    public void updateNext(RoomEndUser next) {
        this.next = next;
    }
    public void updatePrev(RoomEndUser prev) {
        this.prev = prev;
    }
    public String getChannelTitle() {
        return this.channel.getTitle();
    }

}
