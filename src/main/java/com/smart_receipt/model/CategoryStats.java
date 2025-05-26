package com.smart_receipt.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;


@Entity
@Table(name = "category_stats")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CategoryStats {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "category_stats_seq")
    @SequenceGenerator(name = "category_stats_seq", sequenceName = "category_stats_id_seq", allocationSize = 1)
    private Long id;

    @Column(nullable = false)
    private int year;
    @Column(nullable = false)
    private int month;

    @Column(name = "month_total")
    private BigDecimal monthTotal;

    @Column(name = "monthly_limit")
    private BigDecimal monthlyLimit;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    private Category category;
}



