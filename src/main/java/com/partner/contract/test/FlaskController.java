package com.partner.contract.test;

import com.partner.contract.global.exception.dto.SuccessResponse;
import com.partner.contract.global.exception.error.ApplicationException;
import com.partner.contract.global.exception.error.ErrorCode;
import com.partner.contract.global.exception.error.SuccessCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Slf4j
@RestController
public class FlaskController {
    private final RestTemplate restTemplate = new RestTemplate();
    private final String flaskUrl = "http://localhost:5000/flask/contract/{contract_id}/ai-review";

    @PostMapping("/sends3url")
    public ResponseEntity<SuccessResponse<Map>> sends3url(@RequestBody Map<String, String> request) {
        String s3Path = "s3://rising-star-reference-document-bucket/가맹사업거래의 공정화에 관한 법률(법률)(제20712호)(20250121).pdf";
        String category = "R&D";

        if(s3Path == null || s3Path.isEmpty()) {
            throw new ApplicationException(ErrorCode.REQUEST_PARAMETER_MISSING_ERROR);
        }

        // flask로 요청 전송
        try{
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            HttpEntity<Map<String, String>> entity = new HttpEntity<>(request, headers);
            // 추후에 저장할 dto 타입으로 변경해야함
            ResponseEntity<Map> response = restTemplate.exchange(flaskUrl, HttpMethod.POST, entity, Map.class);

            return ResponseEntity.ok(SuccessResponse.of(SuccessCode.SELECT_SUCCESS, response.getBody()));
        } catch (Exception e) {
            throw new ApplicationException(ErrorCode.INTERNAL_SERVER_ERROR);
        }
    }
}
