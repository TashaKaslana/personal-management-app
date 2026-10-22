package com.example.personal_management_app.note.core.service;

import tools.jackson.core.JacksonException;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.json.JsonMapper;
import org.springframework.stereotype.Component;

import java.util.Set;
import java.util.StringJoiner;

/**
 * Derives the plain-text search projection ({@code Note.searchText}) from a note's title
 * and its block-document content.
 * <p>
 * The content is stored as the frontend's NoteContentDocument JSON, where every block is
 * wrapped in its type tag, e.g.
 * {@code {"version":2,"blocks":[{"Text":{"text":"hello"}},{"Checkbox":{"label":"buy milk"}},{"ModelBox":{"title":"panel","blocks":[...]}}]}}.
 * This extractor walks the document with Jackson (no typed mirror of the frontend block
 * hierarchy on purpose) and collects the visible text:
 * <ul>
 *     <li>Text blocks - {@code text}</li>
 *     <li>Checkbox blocks - {@code label}</li>
 *     <li>ModelBox panels - {@code title} plus everything nested inside</li>
 *     <li>Image and unknown/future block types - skipped (forward compatible)</li>
 * </ul>
 * If the content is not a valid document (legacy plain-text note), it is searched as-is.
 */
@Component
public class NoteSearchTextExtractor {

    private static final Set<String> TEXT_BLOCK_TAGS = Set.of("Text", "Checkbox", "ModelBox");

    private final JsonMapper jsonMapper;

    public NoteSearchTextExtractor(JsonMapper jsonMapper) {
        this.jsonMapper = jsonMapper;
    }

    /**
     * Build the plain-text search projection for a note
     */
    public String extract(String title, String content) {
        StringJoiner joiner = new StringJoiner("\n");
        appendNonBlank(joiner, title);

        JsonNode blocks = extractBlocks(content);
        if (blocks == null) {
            // Not a valid block document - treat as legacy plain text
            appendNonBlank(joiner, content);
        } else {
            appendBlockText(blocks, joiner);
        }
        return joiner.toString();
    }

    private JsonNode extractBlocks(String content) {
        if (content == null || content.isBlank()) {
            return null;
        }
        try {
            JsonNode root = jsonMapper.readTree(content);
            if (root != null && root.isObject()) {
                JsonNode blocks = root.get("blocks");
                if (blocks != null && blocks.isArray()) {
                    return blocks;
                }
            }
        } catch (JacksonException ignored) {
            // Not valid JSON - fall back to plain text
        }
        return null;
    }

    private void appendBlockText(JsonNode blocks, StringJoiner joiner) {
        for (JsonNode wrapper : blocks) {
            if (wrapper == null || !wrapper.isObject() || wrapper.isEmpty()) {
                continue;
            }
            wrapper.forEachEntry((tag, block) -> {
                if (!TEXT_BLOCK_TAGS.contains(tag) || block == null || !block.isObject()) {
                    return;
                }
                switch (tag) {
                    case "Text" -> appendNonBlank(joiner, stringValue(block, "text"));
                    case "Checkbox" -> appendNonBlank(joiner, stringValue(block, "label"));
                    case "ModelBox" -> {
                        appendNonBlank(joiner, stringValue(block, "title"));
                        JsonNode nested = block.get("blocks");
                        if (nested != null && nested.isArray()) {
                            appendBlockText(nested, joiner);
                        }
                    }
                    default -> { }
                }
            });
        }
    }

    private String stringValue(JsonNode block, String field) {
        JsonNode value = block.get(field);
        return value != null && value.isString() ? value.asString() : null;
    }

    private void appendNonBlank(StringJoiner joiner, String text) {
        if (text != null && !text.isBlank()) {
            joiner.add(text);
        }
    }
}
