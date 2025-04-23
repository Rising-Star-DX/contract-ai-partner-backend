package com.partner.contract.agreement.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Data;

import java.util.List;

@Data
public class IncorrectTextResponseDto {
    private Long id;
    @JsonProperty("currentPage")
    private Integer page;
    private Double accuracy;
    private String incorrectText;
    private String proofText;
    private String correctedText;
    private List<IncorrectPositionResponseDto> positions;
    private List<IncorrectPositionResponseDto> positionParts;

    public IncorrectTextResponseDto(Long id, Integer page, Double accuracy, String incorrectText, String proofText, String correctedText, String positions, String positionParts) {
        this.id = id;
        this.page = page;
        this.accuracy = accuracy;
        this.incorrectText = incorrectText;
        this.proofText = proofText;
        this.correctedText = correctedText;
        this.positions = parsePositionJson(positions);
        this.positionParts  = parsePositionJson(positionParts);
    }

    private List<IncorrectPositionResponseDto> parsePositionJson(String position) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();

            // 각 하위 배열을 List<Double>로 파싱
            List<List<Double>> parsedPositions = objectMapper.readValue(position, new TypeReference<List<List<Double>>>() {
            });

            // List<List<Double>>를 List<PositionResponseDto>로 변환
            return parsedPositions.stream()
                    .filter(data -> data.size() == 4)
                    .map(data -> new IncorrectPositionResponseDto(
                            data.get(0), // height
                            data.get(1), // width
                            data.get(2), // left
                            data.get(3)  // top
                    ))
                    .toList();
        } catch (Exception e) {
            return List.of(); // 변환 실패 시 빈 리스트 반환
        }
    }
}
