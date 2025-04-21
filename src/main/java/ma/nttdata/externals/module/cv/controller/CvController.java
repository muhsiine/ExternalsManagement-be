package ma.nttdata.externals.module.cv.controller;

import ma.nttdata.externals.module.cv.dto.CvFileDTO;
import ma.nttdata.externals.module.cv.service.CvSrv;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/cv")
public class CvController {

    private static final Logger logger = LoggerFactory.getLogger(CvController.class);

    private final CvSrv cvSrv;

    public CvController(CvSrv cvSrv) {
        this.cvSrv = cvSrv;
    }

    @PostMapping("/extract")
    public ResponseEntity<?> extractCandidateInfo(@RequestBody CvFileDTO cvFileDTO) {
        logger.info("Extracting candidate information from CV");

        var extractedData = cvSrv.extractCandidateInfo(cvFileDTO);
        logger.info("Successfully extracted candidate information from CV");
        return ResponseEntity.ok(extractedData);
    }
}
