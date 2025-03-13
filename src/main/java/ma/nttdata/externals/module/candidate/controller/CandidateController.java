package ma.nttdata.externals.module.candidate.controller;

import ma.nttdata.externals.module.candidate.dto.CandidateDTO;
import ma.nttdata.externals.module.candidate.service.CandidateSrv;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/candidates")
public class CandidateController {

    private final CandidateSrv candidateSrv;

    public CandidateController(CandidateSrv candidateSrv) {
        this.candidateSrv = candidateSrv;
    }

    @PostMapping("/extract")
    public String extractCandidateInfo(@RequestBody CandidateDTO candidate) {
        String encodedBase64File = candidate.cvFiles().getFirst().fileContent();
        return candidateSrv.extractCandidateInfo(encodedBase64File);
    }
    @PostMapping
    public String candidate(@RequestBody CandidateDTO candidate) {
        candidateSrv.save(candidate);
    return "Successfully created";
   }
}
