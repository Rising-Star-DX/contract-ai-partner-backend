package com.partner.contract.test;

import com.partner.contract.global.exception.dto.SuccessResponse;
import com.partner.contract.global.exception.error.ApplicationException;
import com.partner.contract.global.exception.error.ErrorCode;
import com.partner.contract.global.exception.error.SuccessCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
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

    @Value("${secret.flask.local.ip}")
    private String FLASK_SERVER_IP;

    @PostMapping("/sends3url")
    public ResponseEntity<SuccessResponse<Map>> sends3url(@RequestBody Map<String, String> request) {
        String s3Path = "s3://rising-star-reference-document-bucket/테스트용.pdf";
        String category = "R&D";

        String flaskUrl = "http://"+FLASK_SERVER_IP+"/flask/standard"; // 1 -> {contract_id}

        if(s3Path == null || s3Path.isEmpty()) {
            throw new ApplicationException(ErrorCode.REQUEST_PARAMETER_MISSING_ERROR);
        }

        request.put("s3Path", s3Path);
        request.put("category", category);
        request.put("standardId", "1");

        // flask로 요청 전송
//        try{
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            HttpEntity<Map<String, String>> entity = new HttpEntity<>(request, headers);
            System.out.println(flaskUrl);
            // 추후에 저장할 dto 타입으로 변경해야함
            ResponseEntity<Map> response = restTemplate.exchange(flaskUrl, HttpMethod.POST, entity, Map.class);

            return ResponseEntity.ok(SuccessResponse.of(SuccessCode.SELECT_SUCCESS, response.getBody()));
//        } catch (Exception e) {
//            System.out.println(e);
//            throw new ApplicationException(ErrorCode.INTERNAL_SERVER_ERROR);
//        }
    }
}
