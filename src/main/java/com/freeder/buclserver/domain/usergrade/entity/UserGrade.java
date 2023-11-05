package com.freeder.buclserver.domain.usergrade.entity;

import com.freeder.buclserver.core.mixin.TimestampMixin;
import com.freeder.buclserver.domain.usergrade.vo.Grade;
import javax.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "user_grade")
public class UserGrade extends TimestampMixin {
    @Id
    @Column(name = "user_grade_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Enumerated(EnumType.STRING)
    private Grade grade;

    @Column(name = "grade_desc", length = 1000)
    private String gradeDesc;
}
