package com.crestostudio.restaurant_pos.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.EqualsAndHashCode;

@Entity
@Table(name = "restaurants")
@Getter
@Setter
@NoArgsConstructor
@ToString
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Restaurant {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @EqualsAndHashCode.Include
    private UUID id;

    @Column(nullable = false)
    private String name;

    private String email;

    private String phone;

    @Column(columnDefinition = "text")
    private String address;

    private String panNumber;

    private String vatNumber;

    @Column(precision = 5, scale = 2, columnDefinition = "decimal(5,2) default 0")
    private BigDecimal vatRate = BigDecimal.ZERO;

    private String logoUrl;

    @Column(nullable = false)
    private Boolean isActive = true;

    @CreationTimestamp
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;

    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    @OneToMany(mappedBy = "restaurant", cascade = CascadeType.PERSIST)
    private List<User> users = new ArrayList<>();

    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    @OneToMany(mappedBy = "restaurant", cascade = CascadeType.PERSIST)
    private List<DiningTable> diningTables = new ArrayList<>();

    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    @OneToMany(mappedBy = "restaurant", cascade = CascadeType.PERSIST)
    private List<MenuCategory> menuCategories = new ArrayList<>();

    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    @OneToMany(mappedBy = "restaurant", cascade = CascadeType.PERSIST)
    private List<MenuItem> menuItems = new ArrayList<>();

    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    @OneToMany(mappedBy = "restaurant", cascade = CascadeType.PERSIST)
    private List<Order> orders = new ArrayList<>();

    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    @OneToMany(mappedBy = "restaurant", cascade = CascadeType.PERSIST)
    private List<InventoryCategory> inventoryCategories = new ArrayList<>();

    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    @OneToMany(mappedBy = "restaurant", cascade = CascadeType.PERSIST)
    private List<InventoryItem> inventoryItems = new ArrayList<>();
}
