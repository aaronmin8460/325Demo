package common.message;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Collections;
import java.util.List;

final class MessageCodec {

    private static final String FIELD_DELIMITER = "\\|";

    private static final String LIST_DELIMITER = ";";

    private MessageCodec() {
    }

    static String[] split(String serialized) {

        return serialized.split(FIELD_DELIMITER, -1);

    }

    static String encode(String value) {

        String safeValue = value == null ? "" : value;

        return Base64.getEncoder().encodeToString(safeValue.getBytes(StandardCharsets.UTF_8));

    }

    static String decode(String value) {

        if (value == null || value.isEmpty()) {

            return "";

        }

        return new String(Base64.getDecoder().decode(value), StandardCharsets.UTF_8);

    }

    static String encodeList(List<String> values) {

        if (values == null || values.isEmpty()) {

            return "";

        }

        List<String> encodedValues = new ArrayList<>();

        for (String value : values) {

            encodedValues.add(encode(value));

        }

        return String.join(LIST_DELIMITER, encodedValues);

    }

    static List<String> decodeList(String value) {

        if (value == null || value.isEmpty()) {

            return Collections.emptyList();

        }

        String[] tokens = value.split(LIST_DELIMITER, -1);
        List<String> decodedValues = new ArrayList<>();

        for (String token : tokens) {

            decodedValues.add(decode(token));

        }

        return decodedValues;

    }

}
