package dio.budgeting.infrastructure.ai;

import org.springframework.ai.audio.transcription.TranscriptionModel;
import org.springframework.ai.audio.tts.TextToSpeechModel;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

/** Orquestra o fluxo: áudio -> texto -> IA + tools -> resposta -> áudio. */
@Service
public class AssistantService {

    private static final String SYSTEM_PROMPT = """
            Você é um assistente de orçamento pessoal. Responda sempre em português do Brasil,
            de forma curta e natural, pois a resposta será falada em voz alta.
            Para registrar ou consultar transações, use SEMPRE as ferramentas disponíveis;
            nunca invente valores. Se faltar o valor ou o tipo (receita/despesa), pergunte.
            Se uma ferramenta devolver um erro, explique o problema de forma simples.
            A data de hoje é %s. Converta datas como "hoje", "ontem" ou "este mês" para yyyy-MM-dd.
            """;

    private final TranscriptionModel transcriptionModel;
    private final TextToSpeechModel textToSpeechModel;
    private final ChatClient chatClient;

    public AssistantService(TranscriptionModel transcriptionModel,
                            TextToSpeechModel textToSpeechModel,
                            ChatClient.Builder chatClientBuilder,
                            BudgetingTools tools) {
        this.transcriptionModel = transcriptionModel;
        this.textToSpeechModel = textToSpeechModel;
        this.chatClient = chatClientBuilder.defaultTools(tools).build();
    }

    public String transcribe(Resource audio) {
        return transcriptionModel.transcribe(audio);
    }

    public String answer(String userText) {
        return chatClient.prompt()
                .system(SYSTEM_PROMPT.formatted(LocalDate.now()))
                .user(userText)
                .call()
                .content();
    }

    public byte[] speak(String text) {
        return textToSpeechModel.call(text);
    }
}
