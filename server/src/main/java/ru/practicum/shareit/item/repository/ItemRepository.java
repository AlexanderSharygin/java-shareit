package ru.practicum.shareit.item.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.item.model.Item;

import java.util.Collection;
import java.util.List;

public interface ItemRepository extends JpaRepository<Item, Long> {
    List<Item> findByOwner_Id(Long id, Pageable pageable);

    List<Item> findByOwner_Id(Long id);

    @Query("SELECT i FROM Item i WHERE i.itemRequest.id IN ?1")
    List<Item> findByItemRequest_IdIn(Collection<Long> ids);

    @Query("SELECT i FROM Item i " +
            "WHERE UPPER(i.name) LIKE UPPER(CONCAT('%', ?1, '%')) " +
            "OR UPPER(i.description) LIKE UPPER(CONCAT('%', ?2, '%')) AND i.available = true")
    List<Item> findAvailableItemsByNameOrDescription(
            String name, String description, Pageable pageable);
}
