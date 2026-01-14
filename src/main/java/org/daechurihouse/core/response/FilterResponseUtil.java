package org.daechurihouse.core.response;

import java.util.LinkedHashMap;
import java.util.Map;

import org.daechurihouse.core.exception.ErrorCode;

import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class FilterResponseUtil {

	public static void setProblemDetailResponse(HttpServletResponse response, ErrorCode errorCode) {
		try {
			ObjectMapper objectMapper = new ObjectMapper();
			Map<String, Object> problemBody = new LinkedHashMap<>();

			problemBody.put("success", false);
			problemBody.put("code", errorCode.getCode());
			problemBody.put("message", errorCode.getMessage());
			problemBody.put("errors", null);

			response.setStatus(errorCode.getStatus().value());
			response.setContentType("application/problem+json");
			response.setCharacterEncoding("UTF-8");
			response.getOutputStream().write(objectMapper.writeValueAsBytes(problemBody));
		} catch (Exception e) {
			log.error(e.getMessage());
		}
	}
}