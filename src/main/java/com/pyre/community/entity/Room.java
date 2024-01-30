package com.pyre.community.entity;


import com.fasterxml.jackson.annotation.JsonIgnore;
import com.pyre.community.enumeration.RoomType;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Room {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ROOM_ID")
    private long id;
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
