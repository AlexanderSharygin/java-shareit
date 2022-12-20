package ru.practicum.shareit.item.mapper;

import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.model.Comment;

import java.time.LocalDateTime;
import java.time.ZoneId;

public class CommentMapper {

    public static CommentDto toCommentDto(Comment comment) {
        return new CommentDto(
                comment.getId(),
                comment.getText(),
                comment.getAuthor().getName(),
                comment.getCreated());
    }

    public static Comment froCommentDto(CommentDto commentDto) {
        return new Comment(
                -1L,
                commentDto.getText(),
                null,
                null,
                LocalDateTime.now(ZoneId.of("UTC")));
    }
}