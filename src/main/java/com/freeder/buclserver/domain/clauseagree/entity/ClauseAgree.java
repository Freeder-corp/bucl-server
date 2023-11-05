package com.freeder.buclserver.domain.clauseagree.entity;

import com.freeder.buclserver.core.mixin.TimestampMixin;
import com.freeder.buclserver.domain.clause.entity.Clause;
import com.freeder.buclserver.domain.user.entity.User;
import javax.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@Table(name = "clause_agree")
public class ClauseAgree extends TimestampMixin {
    @Id
    @Column(name = "clause_agree_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @ManyToOne
    @JoinColumn(name = "clause_id")
    private Clause clause;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @Column(name = "is_agreed", nullable = false)
    private boolean isAgreed;

    @CreatedDate
    @Column(name = "agreed_at")
    private LocalDateTime agreedAt;
}
