package ru.practicum.shareit.item.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "items")
public class Item {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;
    @Column(nullable = false)
    String name;
    @Column(length = 512)
    String description;
    @Column(nullable = false)
    Boolean available;
    @ManyToOne
    @JoinColumn(name = "owner_id", nullable = false)
    User owner;
    @ManyToOne
    @JoinColumn(name = "request_id")
    ItemRequest request;
}
