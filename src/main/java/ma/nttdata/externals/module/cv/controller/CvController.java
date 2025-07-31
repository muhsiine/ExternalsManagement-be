package ma.nttdata.externals.module.cv.controller;

import ma.nttdata.externals.module.candidate.dto.CandidateDTO;
import ma.nttdata.externals.module.cv.dto.CvFileDTO;
import ma.nttdata.externals.module.cv.service.cvSrv;
import ma.nttdata.externals.module.candidate.service.CandidateSrv;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/cv")
public class CvController {

    private final cvSrv cvSrv;
    private final CandidateSrv candidateSrv;

    public CvController(cvSrv cvSrv , CandidateSrv candidateSrv) {
        this.cvSrv = cvSrv;
        this.candidateSrv = candidateSrv;
    }

    @PostMapping("/extract")
    public ResponseEntity<?> extractCandidateInfo(@RequestBody CvFileDTO cvFileDTO) {
        var  extractedData = cvSrv.extractCandidateInfo(cvFileDTO);
        return ResponseEntity.ok(extractedData);
    }

    @PostMapping("/save")
    public ResponseEntity<?> saveCandidateFromCv(@RequestBody CandidateDTO candidateDTO) {
        try {
            CandidateDTO savedCandidate = candidateSrv.save(candidateDTO);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(savedCandidate);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error saving candidate: " + e.getMessage());
        }
    }

}
