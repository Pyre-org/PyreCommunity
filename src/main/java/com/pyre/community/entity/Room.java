package com.pyre.community.entity;


import com.fasterxml.jackson.annotation.JsonIgnore;
import com.pyre.community.dto.request.RoomUpdateRequest;
import com.pyre.community.enumeration.RoomType;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.GenericGenerator;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Room {
    @Id
    @GeneratedValue(generator = "uuid2")
    @GenericGenerator(name = "uuid2", strategy = "uuid2")
    @Column(name = "ROOM_ID", columnDefinition = "BINARY(16)")
    private UUID id;
    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnore
    @ToString.Exclude
    @JoinColumn(name = "CHANNEL_ID")
    private Channel channel;
//    @OneToMany(mappedBy = "room")
//    private List<RoomChat> roomChats;
    private String title;
    private String description;
    private String imageUrl;
    private String inviteLink;
    private LocalDateTime inviteExpireDate;
    @Enumerated(value = EnumType.STRING)
    private RoomType type;
    @OneToMany(mappedBy = "room", cascade = CascadeType.ALL)
    private List<RoomEndUser> users;
    @OneToMany(mappedBy = "room", cascade = CascadeType.ALL)
    private List<Space> spaces;
    private LocalDateTime cAt;
    private LocalDateTime mAt;

    @Builder
    public Room(
            String title,
            String description,
            String imageUrl,
            Channel channel,
            RoomType type
    ) {
        this.title = title;
        this.description = description;
        this.imageUrl = imageUrl;
        this.type = type;
        this.channel = channel;
        this.users = new ArrayList<>();
        this.spaces = new ArrayList<>();
        this.cAt = LocalDateTime.now();
    }
    public void updateRoom(RoomUpdateRequest roomUpdateRequest) {
        this.title = roomUpdateRequest.title();
        this.description = roomUpdateRequest.description();
        this.imageUrl = roomUpdateRequest.imageUrl();
        this.type = roomUpdateRequest.type();
        this.mAt = LocalDateTime.now();
    }
    public void updateInvite(String inviteLink, LocalDateTime inviteExpireDate) {
        this.inviteLink = inviteLink;
        this.inviteExpireDate = inviteExpireDate;
    }
}
