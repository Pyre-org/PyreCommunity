package com.pyre.community.entity;

import com.pyre.community.enumeration.ApprovalStatus;
import com.pyre.community.enumeration.ChannelGenre;
import com.pyre.community.enumeration.ChannelType;
import jakarta.persistence.*;
import jakarta.validation.constraints.Pattern;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
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

    public void updateTitle(String title) {
        this.title = title;
        this.mAt = LocalDateTime.now();
    }
    public void updateDescription(String description) {
        this.description = description;
        this.mAt = LocalDateTime.now();
    }
    public void updateGenre(ChannelGenre genre) {
        this.genre = genre;
        this.mAt = LocalDateTime.now();
    }
    public void updateImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
        this.mAt = LocalDateTime.now();
    }
    public void updateApprovalStatus(ApprovalStatus approvalStatus) {
        this.approvalStatus = approvalStatus;
        this.mAt = LocalDateTime.now();
    }

    public void updateChannel(
            String title,
            String description,
            ChannelGenre genre,
            String imageUrl
    ) {
        this.title = title;
        this.description = description;
        this.genre = genre;
        this.imageUrl = imageUrl;
        this.mAt = LocalDateTime.now();
    }
    public int getMemberCounts() {
        return this.endUsers.size();
    }
    public int getRoomCounts() {
        return this.rooms.size();
    }
    @Builder
    public Channel(
            String title,
            String description,
            ChannelGenre genre,
            String imageUrl
    ) {
        this.title = title;
        this.description = description;
        this.genre = genre;
        this.imageUrl = imageUrl;
        this.rating = 0.0f;
        this.endUsers = new HashSet<>();
        this.rooms = new ArrayList<>();
        this.type = ChannelType.CHANNEL_PUBLIC;
        this.cAt = LocalDateTime.now();
        this.approvalStatus = ApprovalStatus.CHECKING;
    }
}
