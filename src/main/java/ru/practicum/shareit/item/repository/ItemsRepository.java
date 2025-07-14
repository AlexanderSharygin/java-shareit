package ru.practicum.shareit.item.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemsRepository extends JpaRepository<Item, Long> {
    List<Item> findAllByOwner_Id(Long ownerId);

    @Query("select i from Item i " +
            "where (upper(i.name) like upper(:name) or upper(i.description) " +
            "like upper(:description)) and i.available=true ")
    List<Item> findAllAvailableByNameLikeIgnoreCaseOrDescriptionLikeIgnoreCase(String name, String description);

    List<Item> findAllByOwnerId(long userId);
}
