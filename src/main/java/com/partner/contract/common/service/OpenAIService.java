package com.partner.contract.common.service;

import com.partner.contract.common.dto.GptMessageRequestDto;
import com.partner.contract.common.dto.GptRequestDto;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

@Service
@RequiredArgsConstructor
public class OpenAIService {

    private final WebClient openAIConfig;

    @Value("${secret.openAI.version}")
    private String model;

    @Async("gptAsyncExecutor")
    public CompletableFuture<String> callPrompt(List<GptMessageRequestDto> prompt) {
        GptRequestDto gptRequestDto = GptRequestDto.builder()
                .messages(prompt)
                .model(model)
                .top_p(1.0)
                .temperature(0.5)
                .build();

//        System.out.println("gptRequestDto = " + gptRequestDto);

        return openAIConfig.post()
                .uri("/v1/chat/completions")
                .bodyValue(gptRequestDto)
                .retrieve()
                .bodyToMono(String.class)
                .toFuture();  // 비동기 처리
    }

    public String makeAgreementAnalysisPrompt(String clauseContent, List<String> proofTexts, List<String> incorrectTexts, List<String> correctedTexts) {
        // ✅ JSON 데이터 생성
        Map<String, Object> inputData = Map.of(
                "clause_content", clauseContent,
                "proof_texts", proofTexts,
                "incorrect_texts", incorrectTexts,
                "corrected_texts", correctedTexts
        );

        return String.format("""
                    예시 위배 문장과 예시 위배 교정 문장을 참고해서 
                    입력받은 계약서 중 틀린 문장을 기준 문서(법률 조항)과 비교하여 교정해줘
                    입력받은 텍스트에서 '\\n' 엔터키는 지우고 작업 수행해.
                    그리고 참고한 자료를 기반으로 위배된 확률을 계산해줘
                    틀린 확률이 높다면 accuracy 를 높여줘
                    accuracy 0~1 범위의 float 형태로 출력해줘
                    반드시 JSON 코드 블록 (```json ...) 을 사용하지 말고, 그냥 JSON 객체만 반환해.
                    맞춤법, 띄어쓰기를 수정하지 말고 계약서 내용에서 틀린걸 수정해줘
                    교정 전 후 값이 일치하거나 의미차이가 없다면 데이터 출력 하지 말아줘

                    [입력 데이터 설명]
                    - clause_content: 사용자가 입력한 계약서의 문장 (수정해야 하는 문장)
                    - proof_texts: 기준이 되는 법률 문서의 문장 목록 (계약서와 비교할 법률 조항들)
                    - incorrect_texts: 법률을 위반할 가능성이 있는 문장 예시 목록
                    - corrected_texts: 법률 위반 가능성이 있는 예시 문장을 올바르게 수정한 문장 목록

                    [입력 데이터]
                    %s

                    [출력 형식]
                    {
                        "clause_content": "계약서 원문",
                        "corrected_text": "계약서의 문장을 올바르게 교정한 문장",
                        "proof_text": "입력데이터를 참조해 잘못된 포인트와 이유",
                        "accuracy": "위배된 비율, 신뢰도"
                    }

                    [조건]
                    - 위반 문장과 교정 문장은 서로 논리적으로 연결되어야 함,
                    - 결과는 반드시 JSON 형식으로 반환해
                """, inputData.toString());
    }
}
