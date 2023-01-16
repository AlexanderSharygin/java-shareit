package ru.practicum.shareit.request.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.request.model.ItemRequest;

import java.util.List;

public interface ItemRequestRepository extends JpaRepository<ItemRequest, Long> {
    @Query("SELECT i FROM ItemRequest i WHERE i.owner.id <> ?1 ORDER BY i.createDateTime DESC")
    List<ItemRequest> findByOwner_IdNotOrderByCreateDateTimeDesc(Long id, Pageable pageable);

    List<ItemRequest> findByOwner_IdOrderByCreateDateTimeDesc(Long id);
}