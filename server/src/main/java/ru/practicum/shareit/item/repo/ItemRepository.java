package ru.practicum.shareit.item.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

@Repository
public interface ItemRepository extends JpaRepository<Item, Long> {
    List<Item> findByOwnerId(Long id);

    @Query("select i from Item as i where i.request.id in :requestIds")
    List<Item> findAllByRequestId(@Param("requestIds") List<Long> requestIds);

    List<Item> findByRequestId(Long requestId);

}
