package ma.nttdata.externals.module.cv.service;

import ma.nttdata.externals.module.cv.dto.CvFileDTO;

public interface CvSrv {
    String extractCandidateInfo(CvFileDTO cvFileDTO);
}
