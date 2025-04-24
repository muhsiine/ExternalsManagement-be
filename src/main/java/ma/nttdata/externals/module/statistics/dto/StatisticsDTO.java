package ma.nttdata.externals.module.statistics.dto;

import java.util.Map;

public record StatisticsDTO(
    Map<String, Integer> experienceDistribution,
    int totalCandidates,
    double averageExperience
) {}