package org.hwmoodle.dto;

import java.util.Map;

public record ErrorResponse(String message, Map<String, String> fieldErrors) {
}

