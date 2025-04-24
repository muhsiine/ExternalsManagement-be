package ma.nttdata.externals.module.statistics.service.impl;

import lombok.RequiredArgsConstructor;
import ma.nttdata.externals.module.statistics.dto.StatisticsDTO;
import ma.nttdata.externals.module.candidate.service.CandidateSrv;

import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;

import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.IntSummaryStatistics;
import java.util.LinkedHashMap;
import ma.nttdata.externals.module.statistics.service.StatisticsService;

@Slf4j
@Service
@RequiredArgsConstructor
public class StatisticsServiceImpl implements StatisticsService {
    // private static final Logger log = LoggerFactory.getLogger(StatisticsServiceImpl.class);

    private final CandidateSrv candidateSrv;

    @Override
    public StatisticsDTO getExperienceStats() {
        var candidates = candidateSrv.getAllCandidates();

        Map<String, Integer> distribution = candidates.stream()
            .collect(Collectors.groupingBy(
                candidate -> bucketKey(candidate.yearsOfExperience()),
                Collectors.collectingAndThen(Collectors.counting(), Long::intValue)
            ));


        IntStream.rangeClosed(1,10)
            .forEach(i -> distribution.putIfAbsent(key(i), 0));

        IntSummaryStatistics stats = candidates.stream()
            .mapToInt(c->c.yearsOfExperience()).summaryStatistics(); 

        double averageExperience = stats.getAverage();

        return new StatisticsDTO(
            distribution,
            (int) stats.getCount(),
            averageExperience
        );
    }

    private String bucketKey(int years) {
        return years >= 10 ? "10+" : String.valueOf(years);
    }

    private String key(int years) {
        return  years ==  10 ? "10+" : String.valueOf(years);
    }
}