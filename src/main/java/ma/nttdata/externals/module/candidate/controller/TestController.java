package ma.nttdata.externals.module.candidate.controller;

import ma.nttdata.externals.module.ai.service.LlmService;
import ma.nttdata.externals.module.candidate.dto.CandidateDTO;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;

@RestController
public class TestController {

    private final LlmService llmService;

    public TestController(LlmService llmService) {
        this.llmService = llmService;
    }

    @GetMapping("/hello")
    public String hello() {
        return "hello";
    }

    @PostMapping("/prompt")
    public String prompt(@RequestBody String prompt) {
        return llmService.call(prompt);
    }

    @GetMapping(value = "/stream", produces = "text/event-stream")
    public Flux<String> promptStream(@RequestParam String prompt) {
        return llmService.callStream(prompt);
    }

    @PostMapping("/candidate")
    public String condidate(@RequestBody CandidateDTO candidate) {

        return "Success";
    }
}
