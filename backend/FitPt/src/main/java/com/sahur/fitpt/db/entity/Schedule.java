package com.sahur.fitpt.db.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "schedule")
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Schedule {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long scheduleId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "trainer_id")
    private Trainer trainer;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    //@CreationTimestamp
    @Column(name = "start_time")
    private LocalDateTime startTime;

    //@CreationTimestamp
    @Column(name = "end_time")
    private LocalDateTime endTime;

    @Column(name = "schedule_content", columnDefinition = "TEXT")
    private String scheduleContent;

    public void update(Member member, Trainer trainer, LocalDateTime startTime,
                       LocalDateTime endTime, String scheduleContent) {
        this.member = member;
        this.trainer = trainer;
        this.startTime = startTime;
        this.endTime = endTime;
        this.scheduleContent = scheduleContent;
    }
}


