package ru.practicum.shareit.comment.dto;

import ru.practicum.shareit.comment.model.Comment;

import java.time.LocalDateTime;
import java.time.ZoneId;

public class CommentMapper {

    public static CommentDto toCommentDto(Comment comment) {
        return new CommentDto(
                comment.getId(),
                comment.getText(),
                comment.getAuthor().getName(),
                comment.getCreatedTime());
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