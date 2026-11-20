package com.example.personal_management_app.note.tag.exception;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.context.request.ServletWebRequest;

import static org.assertj.core.api.Assertions.assertThat;

class TagSpecificExceptionHandlerTest {
    @Test
    void handleTagOwnerUnavailableException_shouldReturnServerError_whenOwnerCannotBeResolved() {
        var handler = new TagSpecificExceptionHandler();
        var request = new ServletWebRequest(new MockHttpServletRequest("GET", "/api/tags"));

        var response = handler.handleTagOwnerUnavailableException(new TagOwnerUnavailableException(), request);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().isSuccess()).isFalse();
        assertThat(response.getBody().getMessage()).isEqualTo("Tag owner is unavailable");
    }
}
