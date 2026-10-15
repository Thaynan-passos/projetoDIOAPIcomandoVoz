package dio.budgeting.infrastructure.web;

import dio.budgeting.infrastructure.ai.AssistantService;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/api/assistant")
public class AssistantController {

    public record TextRequest(String message) {
    }

    public record TextResponse(String transcript, String answer) {
    }

    private final AssistantService assistant;

    public AssistantController(AssistantService assistant) {
        this.assistant = assistant;
    }

    /** Fluxo completo: recebe áudio e devolve a resposta em áudio (MP3). */
    @PostMapping(value = "/voice", consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = "audio/mpeg")
    public ResponseEntity<byte[]> voice(@RequestParam("audio") MultipartFile audio) throws IOException {
        String filename = audio.getOriginalFilename() != null ? audio.getOriginalFilename() : "audio.mp3";
        ByteArrayResource resource = new ByteArrayResource(audio.getBytes()) {
            @Override
            public String getFilename() {
                return filename; // a OpenAI usa a extensão para identificar o formato
            }
        };
        String transcript = assistant.transcribe(resource);
        String answer = assistant.answer(transcript);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"resposta.mp3\"")
                .body(assistant.speak(answer));
    }

    /** Mesmo fluxo, só com texto: útil para testar sem gravar áudio. */
    @PostMapping("/text")
    public TextResponse text(@RequestBody TextRequest request) {
        return new TextResponse(request.message(), assistant.answer(request.message()));
    }
}
