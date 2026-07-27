package com.obri_back.obri.global.exception;

import com.obri_back.obri.global.common.APIResponse;
import org.junit.jupiter.api.Test;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.assertj.core.api.Assertions.assertThat;

class GlobalExceptionHandlerTest {

    private final GlobalExceptionHandler handler = new GlobalExceptionHandler();

    @Test
    void handleDataIntegrityViolationException_returns409() {
        DataIntegrityViolationException e =
                new DataIntegrityViolationException("FK constraint violation");

        ResponseEntity<APIResponse<Void>> response =
                handler.handleDataIntegrityViolationException(e);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
        assertThat(response.getBody().getStatus()).isEqualTo(409);
        assertThat(response.getBody().getMessage())
                .isEqualTo("연관된 데이터가 있어 처리할 수 없습니다");
    }
}
