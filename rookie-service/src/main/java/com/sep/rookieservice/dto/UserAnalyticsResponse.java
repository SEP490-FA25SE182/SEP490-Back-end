package com.sep.rookieservice.dto;

import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserAnalyticsResponse {
    private long totalUsers;
    private List<RoleCount> byRole;
    private List<ActiveCount> byIsActived;
    private List<MonthlySignup> monthlySignups;

    @Data
    @NoArgsConstructor
    public static class RoleCount {
        private String roleId;
        private String roleName;
        private long count;
    }

    @Data
    @NoArgsConstructor
    public static class ActiveCount {
        private String status; // ACTIVE / INACTIVE
        private long count;
    }

    @Data
    @NoArgsConstructor
    public static class MonthlySignup {
        private int year;
        private int month; // 1..12
        private long count;
    }
}
