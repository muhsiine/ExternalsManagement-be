package ma.nttdata.externals.module.interview.service;

import ma.nttdata.externals.module.interview.dto.InterviewRequestDTO;

public interface InterviewSrv {
    /**
     * Generates interview questions based on candidate profile and specified parameters
     * @param request The interview request containing candidate ID, difficulty level, and main technology
     * @return A string containing the generated interview questions
     */
    String generateInterviewQuestions(InterviewRequestDTO request);
} 