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

    List<Item> findByItemRequest_IdIn(Collection<Long> ids);

    @Query("select i from Item i " +
            "where upper(i.name) like upper(concat('%', ?1, '%')) " +
            "or upper(i.description) like upper(concat('%', ?2, '%')) and i.available = true")
    List<Item> findAvailableItemsByNameOrDescription(
            String name, String description, Pageable pageable);
}
