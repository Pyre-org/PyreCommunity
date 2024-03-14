package com.pyre.community.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.pyre.community.dto.request.SpaceUpdateRequest;
import com.pyre.community.enumeration.SpaceRole;
import com.pyre.community.enumeration.SpaceType;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.GenericGenerator;

import java.util.Objects;
import java.util.UUID;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Space extends BaseEntity {
    @Id
    @GeneratedValue(generator = "uuid2")
    @GenericGenerator(name = "uuid2", strategy = "uuid2")
    @Column(name = "SPACE_ID", columnDefinition = "BINARY(16)")
    private UUID id;
    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnore
    @ToString.Exclude
    @JoinColumn(name = "ROOM_ID")
    private Room room;
    @Enumerated(value = EnumType.STRING)
    private SpaceType type;  // 채팅 OR 피드 스페이스
    @Enumerated(value = EnumType.STRING)
    private SpaceRole role;  // 스페이스를 볼 수 있는 권한의 시작
    private UUID prevId;
    private UUID nextId;
    private String title;
    private String description;
    private Boolean isDeleted;
    @Builder
    public Space(
            Room room,
            SpaceType type,
            SpaceRole role,
            UUID prevId,
            String title,
            String description
    ) {
        this.room = room;
        this.type = type;
        this.role = role;
        this.prevId = prevId;
        this.nextId = null;
        this.title = title;
        this.isDeleted = false;
        this.description = description;
    }
    public void updateNext(UUID nextId) {
        this.nextId = nextId;
    }
    public void updateIsDeleted(Boolean isDeleted) {
        this.isDeleted = isDeleted;
    }
    public void updatePrev(UUID prevId) {
        this.prevId = prevId;
    }
    public void updateSpace(SpaceUpdateRequest spaceUpdateRequest) {
        this.role = SpaceRole.valueOf(spaceUpdateRequest.role().getKey());
        this.title = spaceUpdateRequest.title();
        this.description = spaceUpdateRequest.description();
    }


}
