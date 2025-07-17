package ru.practicum.shareit.comment.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.comment.model.Comment;

import java.util.Collection;
import java.util.List;

public interface CommentsRepository extends JpaRepository<Comment, Long> {
    @Query("select c from Comment c where c.item.id in ?1")
    List<Comment> findCommentsForItems(Collection<Long> ids);
}
