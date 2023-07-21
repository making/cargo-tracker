package se.citerus.dddsample.interfaces.handling;

import org.assertj.core.api.InstanceOfAssertFactories;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EmptySource;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;
import se.citerus.dddsample.domain.model.cargo.TrackingId;
import se.citerus.dddsample.domain.model.handling.HandlingEvent;
import se.citerus.dddsample.domain.model.location.UnLocode;
import se.citerus.dddsample.domain.model.voyage.VoyageNumber;

import java.time.Instant;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.*;

class HandlingReportParserTest {

    @ParameterizedTest
    @NullSource
    void shouldThrowErrorOnParsingNullUnloCode(String input) {
        assertThatThrownBy(() -> HandlingReportParser.parseUnLocode(input))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Failed to parse UNLO code: null");
    }

    @ParameterizedTest
    @EmptySource
    void shouldThrowErrorOnParsingEmptyUnloCode(String input) {
        assertThatThrownBy(() -> HandlingReportParser.parseUnLocode(input))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Failed to parse UNLO code: ");
    }

    @ParameterizedTest
    @ValueSource(strings = {"XXX"})
    void shouldThrowErrorOnParsingInvalidUnloCode(String input) {
        assertThatThrownBy(() -> HandlingReportParser.parseUnLocode(input))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Failed to parse UNLO code: XXX");
    }

    @ParameterizedTest
    @ValueSource(strings = {"SESTO"})
    void shouldReturnUnloCodeOnParsingValidUnloCode(String input) {
        UnLocode result = HandlingReportParser.parseUnLocode(input);
        assertThat(result).isNotNull().extracting("unlocode", as(InstanceOfAssertFactories.STRING)).contains(input);
    }

    @ParameterizedTest
    @NullSource
    void shouldThrowErrorOnParsingNullTrackingId(String input) {
        assertThatThrownBy(() -> HandlingReportParser.parseTrackingId(input))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Failed to parse trackingId: null");
    }

    @ParameterizedTest
    @EmptySource
    void shouldThrowErrorOnParsingEmptyTrackingId(String input) {
        assertThatThrownBy(() -> HandlingReportParser.parseTrackingId(input))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Failed to parse trackingId: ");
    }

    @ParameterizedTest
    @ValueSource(strings = {"ABC123"})
    void shouldReturnTrackingIdOnParsingValidTrackingId(String input) {
        TrackingId result = HandlingReportParser.parseTrackingId(input);
        assertThat(result).isNotNull().extracting("id", as(InstanceOfAssertFactories.STRING)).contains(input);
    }

    @ParameterizedTest
    @NullSource
    void shouldReturnNullOnParsingNullVoyageNumber(String input) {
        VoyageNumber voyageNumber = HandlingReportParser.parseVoyageNumber(input);
        assertThat(voyageNumber).isNull();
    }

    @ParameterizedTest
    @EmptySource
    void shouldReturnNullOnParsingEmptyVoyageNumber(String input) {
        VoyageNumber voyageNumber = HandlingReportParser.parseVoyageNumber(input);
        assertThat(voyageNumber).isNull();
    }

    @ParameterizedTest
    @ValueSource(strings = {"0101"})
    void shouldReturnVoyageNumberOnParsingValidVoyageNumber(String input) {
        VoyageNumber result = HandlingReportParser.parseVoyageNumber(input);
        assertThat(result).isNotNull().extracting("number", as(InstanceOfAssertFactories.STRING)).contains(input);
    }

    @ParameterizedTest
    @NullSource
    void shouldThrowErrorOnParsingNullHandlingEventType(String input) {
        assertThatThrownBy(() -> HandlingReportParser.parseEventType(input))
                .isInstanceOf(NullPointerException.class)
                .hasMessage("Name is null");
    }

    @ParameterizedTest
    @EmptySource
    void shouldThrowErrorOnParsingEmptyHandlingEventType(String input) {
        assertThatThrownBy(() -> HandlingReportParser.parseEventType(input))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage(" is not a valid handling event type. Valid types are: [LOAD, UNLOAD, RECEIVE, CLAIM, CUSTOMS]");
    }

    @ParameterizedTest
    @ValueSource(strings = {"XXX"})
    void shouldThrowErrorOnParsingInvalidHandlingEventType(String input) {
        assertThatThrownBy(() -> HandlingReportParser.parseEventType(input))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("XXX is not a valid handling event type. Valid types are: [LOAD, UNLOAD, RECEIVE, CLAIM, CUSTOMS]");
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "LOAD",
            "UNLOAD",
            "RECEIVE",
            "CLAIM",
            "CUSTOMS"
    })
    void shouldReturnHandlingEventTypeOnParsingValidHandlingEventType(String input) {
        HandlingEvent.Type result = HandlingReportParser.parseEventType(input);
        assertThat(result).isNotNull();
        assertThat(result.name()).isEqualTo(input);
    }

    @ParameterizedTest
    @NullSource
    void shouldThrowErrorOnParsingNullDate(String input) {
        assertThatThrownBy(() -> HandlingReportParser.parseDate(input))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Invalid date format: null, must be on ISO 8601 format: yyyy-MM-dd HH:mm");
    }

    @ParameterizedTest
    @EmptySource
    void shouldThrowErrorOnParsingEmptyDate(String input) {
        assertThatThrownBy(() -> HandlingReportParser.parseDate(input))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Invalid date format: , must be on ISO 8601 format: yyyy-MM-dd HH:mm");
    }

    @ParameterizedTest
    @ValueSource(strings = {"XXX"})
    void shouldThrowErrorOnParsingInvalidDate(String input) {
        assertThatThrownBy(() -> HandlingReportParser.parseDate(input))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Invalid date format: XXX, must be on ISO 8601 format: yyyy-MM-dd HH:mm");
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "2022-10-29 13:37"
    })
    void shouldReturnDateOnParsingValidDate(String input) {
        Instant result = HandlingReportParser.parseDate(input);
        assertThat(result).isNotNull();
    }

    @ParameterizedTest
    @NullSource
    void shouldThrowErrorOnParsingNullCompletionTime(LocalDateTime input) {
        assertThatThrownBy(() -> HandlingReportParser.parseCompletionTime(input))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Completion time is required");
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "2022-01-02T03:04:05"
    })
    void shouldReturnDateOnParsingValidCompletionTime(String input) {
        Instant result = HandlingReportParser.parseCompletionTime(LocalDateTime.parse(input));
        assertThat(result).isNotNull();
    }
}