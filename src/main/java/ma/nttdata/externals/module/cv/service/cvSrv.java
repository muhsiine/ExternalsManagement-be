package ma.nttdata.externals.module.cv.service;

import ma.nttdata.externals.module.cv.dto.CvFileDTO;

public interface cvSrv {
    String extractCandidateInfo(CvFileDTO cvFileDTO);
}
