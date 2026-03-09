package fr.limayrac.demo.dto;

import java.util.List;

public record DashboardResponse(
    long activeProjectsCount,
    long totalTasksCount,
    long overdueTasksCount,
    List<ProjectListResponse> latestProjects
) {}
