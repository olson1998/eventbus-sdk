package com.olsonsolution.eventbus.domain.model;

import com.olsonsolution.eventbus.domain.model.exception.InvalidEventDestinationPatternException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class StandardEventDestinationTest {

    @Test
    void testFromString_ValidInput_Success() {
        // Arrange
        String validDestinationString = "prod.A.pubName.B.subName.entity.command";

        // Act
        StandardEventDestination result = StandardEventDestination.fromString(validDestinationString);

        // Assert
        Assertions.assertNotNull(result);
        Assertions.assertEquals("prod", result.getProduct());
        Assertions.assertEquals(MemberTypes.fromIdentifier('A'), result.getPublisher().getType());
        Assertions.assertEquals("pubName", result.getPublisher().getName());
        Assertions.assertEquals(MemberTypes.fromIdentifier('B'), result.getSubscriber().getType());
        Assertions.assertEquals("subName", result.getSubscriber().getName());
        Assertions.assertEquals("entity", result.getEntity());
        Assertions.assertEquals("command", result.getCommand());
    }

    @Test
    void testFromString_InvalidPattern_ThrowsException() {
        // Arrange
        String invalidDestinationString = "invalid_string";

        // Act & Assert
        Assertions.assertThrows(InvalidEventDestinationPatternException.class,
                () -> StandardEventDestination.fromString(invalidDestinationString));
    }

    @Test
    void testFromString_MissingComponents_ThrowsException() {
        // Arrange
        String missingComponents = "prod.A.pubName";

        // Act & Assert
        Assertions.assertThrows(InvalidEventDestinationPatternException.class,
                () -> StandardEventDestination.fromString(missingComponents));
    }

    @Test
    void testFromString_EmptyInput_ThrowsException() {
        // Arrange
        String emptyDestinationString = "";

        // Act & Assert
        Assertions.assertThrows(InvalidEventDestinationPatternException.class,
                () -> StandardEventDestination.fromString(emptyDestinationString));
    }

    @Test
    void testFromString_NullInput_ThrowsException() {
        // Act & Assert
        Assertions.assertThrows(NullPointerException.class,
                () -> StandardEventDestination.fromString(null));
    }
}