package com.sep.rookieservice.dto;

import com.sep.rookieservice.enums.IsActived;
import lombok.Data;

import java.time.Instant;
import java.util.List;

@Data
public class BookAnalyticsResponse {

    private long totalBooks;
    private List<ActiveCount> byIsActived;
    private List<RecentBook> newestBooks;
    private List<RecentBook> recentlyUpdatedBooks;
    private List<MonthlyCreated> monthlyCreated;

    @Data
    public static class ActiveCount {
        private String status;
        private long count;
    }

    @Data
    public static class RecentBook {
        private String bookId;
        private String bookName;
        private String authorName;
        private Instant createdAt;
        private Instant updatedAt;
    }

    @Data
    public static class MonthlyCreated {
        private int year;
        private int month;
        private long count;
    }
}
