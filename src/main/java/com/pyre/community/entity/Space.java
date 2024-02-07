package com.pyre.community.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.pyre.community.enumeration.SpaceRole;
import com.pyre.community.enumeration.SpaceType;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.GenericGenerator;

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
    private SpaceType type;  // 채팅 OR 피드 스페이스
    private SpaceRole role;  // 스페이스를 볼 수 있는 권한의 시작
    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private Space prev;
    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private Space next;
    @Builder
    public Space(
            Room room,
            SpaceType type,
            SpaceRole role,
            Space prev
    ) {
        this.room = room;
        this.type = type;
        this.role = role;
        this.prev = prev;
        this.next = null;
    }


}
