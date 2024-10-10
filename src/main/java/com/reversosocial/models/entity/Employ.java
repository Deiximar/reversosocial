package com.reversosocial.models.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "employs")
public class Employ {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "position", length = 100, nullable = false)
    private String position;

    @Column(name = "curriculum", length = 100, nullable = false)
    private String curriculum;

    @Column(name = "description", length = 500, nullable = false)
    private String description;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name = "sector_id", nullable = false)
    private Sector sector;

}
