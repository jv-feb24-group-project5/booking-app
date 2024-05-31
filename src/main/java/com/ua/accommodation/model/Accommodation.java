package com.ua.accommodation.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.util.Set;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.SQLDelete;

@Entity
@Table(name = "accommodations")
@SQLDelete(sql = "UPDATE accommodations SET is_deleted = true WHERE id=?")
@Getter
@Setter
public class Accommodation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private Type type;

    @OneToOne
    @JoinColumn(name = "address_id", referencedColumnName = "id", nullable = false)
    private Address location;

    @Column(nullable = false)
    private String size;

    @ManyToMany
    @JoinTable(
            name = "accommodations_amenities",
            joinColumns = @JoinColumn(name = "accommodation_id"),
            inverseJoinColumns = @JoinColumn(name = "amenity_id")
    )

    private Set<Amenity> amenities;

    @Column(name = "daily_rate", nullable = false)
    private BigDecimal dailyRate;

    @Column(nullable = false)
    private Integer availability;

    @Column(nullable = false, name = "is_deleted")
    private boolean isDeleted;

    public enum Type {
        HOUSE, APARTMENT, CONDO, VACATION_HOME
    }
}
