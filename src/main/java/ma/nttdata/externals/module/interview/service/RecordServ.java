package ma.nttdata.externals.module.interview.service;

import ma.nttdata.externals.module.interview.dto.CreateRecordRequestDTO;
import ma.nttdata.externals.module.interview.entity.Record;

public interface RecordServ {
    Record createRecord(CreateRecordRequestDTO request);
}
