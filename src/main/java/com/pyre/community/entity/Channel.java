package com.pyre.community.entity;

import com.pyre.community.enumeration.ApprovalStatus;
import com.pyre.community.enumeration.ChannelGenre;
import com.pyre.community.enumeration.ChannelType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Data
@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Channel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "CHANNEL_ID")
    private long id;
    private String title;
    private String description;
    @Enumerated(EnumType.STRING)
    private ChannelGenre genre;
    private String imageUrl;
    private float rating;
    @OneToMany(mappedBy = "channel", cascade = CascadeType.ALL)
    private Set<ChannelEndUser> endUsers;
    @OneToMany(mappedBy = "channel", cascade = CascadeType.ALL)
    private List<Room> rooms;
    private ChannelType type;
    private LocalDateTime cAt;
    private LocalDateTime mAt;
    @Enumerated(EnumType.STRING)
    private ApprovalStatus approvalStatus;

    public static Channel createChannel(
            String title,
            String description,
            ChannelGenre genre,
            String imageUrl
    ) {
        Channel channel = new Channel();
        channel.setTitle(title);
        channel.setDescription(description);
        channel.setGenre(genre);
        channel.setImageUrl(imageUrl);
        channel.setRating(0.0f);
        channel.setEndUsers(new HashSet<>());
        channel.setRooms(new ArrayList<>());
        channel.setType(ChannelType.CHANNEL_PUBLIC);
        channel.setCAt(LocalDateTime.now());
        channel.setApprovalStatus(ApprovalStatus.CHECKING);
        return channel;
    }
    public static Channel updateChannel(
            String title,
            String description,
            ChannelGenre genre,
            String imageUrl,
            Channel channel
    ) {
        channel.setTitle(title);
        channel.setDescription(description);
        channel.setGenre(genre);
        channel.setImageUrl(imageUrl);
        channel.setMAt(LocalDateTime.now());
        return channel;
    }
}
