package ma.nttdata.externals.module.cv.controller;

import ma.nttdata.externals.module.cv.dto.CvFileDTO;
import ma.nttdata.externals.module.cv.service.cvSrv;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/cv")
public class CvController {

    private final cvSrv cvSrv;

    public CvController(cvSrv cvSrv) {
        this.cvSrv = cvSrv;
    }

    @PostMapping("/extract")
    public ResponseEntity<?> extractCandidateInfo(@RequestBody CvFileDTO cvFileDTO) {
        var  extractedData = cvSrv.extractCandidateInfo(cvFileDTO);
        return ResponseEntity.ok(extractedData);
    }


}
