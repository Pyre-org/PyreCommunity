package com.pyre.community.entity;


import com.fasterxml.jackson.annotation.JsonIgnore;
import com.pyre.community.enumeration.RoomType;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.GenericGenerator;

import java.time.LocalDateTime;
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
    private RoomType type;
    @OneToMany(mappedBy = "room", cascade = CascadeType.ALL)
    private List<RoomEndUser> users;
    private LocalDateTime cAt;
    private LocalDateTime mAt;


}
