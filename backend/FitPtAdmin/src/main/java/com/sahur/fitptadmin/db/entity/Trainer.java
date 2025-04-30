package com.sahur.fitptadmin.db.entity;

import com.sahur.fitptadmin.domain.trainer.dto.TrainerUpdateRequestDto;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "trainer")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Trainer {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "trainer_id")
    private Long trainerId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "admin_id", nullable = false)
    private Admin admin;

    @Column(name = "trainer_name", nullable = false)
    private String trainerName;

    @Column(name = "trainer_login_id", nullable = false)
    private String trainerLoginId;

    @Column(name = "trainer_pw", nullable = false)
    private String trainerPw;

    @Column(name = "trainer_birth_date")
    private LocalDate trainerBirthDate;

    @OneToMany(mappedBy = "trainer")
    private List<Member> members = new ArrayList<>();

    public void updateTrainerInfo(String trainerName, LocalDate birthDate) {
        this.trainerName = trainerName;
        this.trainerBirthDate = birthDate;
    }

}
