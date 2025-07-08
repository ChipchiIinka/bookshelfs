package io.petprojects.bookshelfs.model.response;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;

@Data
@Builder
public class BookShareNotificationResponse {
    private Long id;
    private String bookTitle;
    private String requesterName;
    private String ownerName;
    private String comment;
    private String rejectionReason;
    private String shareStatus;
    private String accessType;
    private LocalDate expirationDate;
}
