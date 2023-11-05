package com.freeder.buclserver.domain.clause.entity;

import com.freeder.buclserver.core.mixin.TimestampMixin;
import javax.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "CLAUSE")
public class Clause extends TimestampMixin {
    @Id
    @Column(name = "clause_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "clause_code", unique = true)
    private String clauseCode;
    
    private String name;

    @Column(length = 3000)
    private String content;

    @Column(name = "is_required", nullable = false)
    private boolean isRequired;
}
