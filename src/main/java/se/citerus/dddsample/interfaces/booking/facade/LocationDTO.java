package se.citerus.dddsample.interfaces.booking.facade;

import java.io.Serializable;

/**
 * Location DTO.
 */
public record LocationDTO(String unLocode, String name) implements Serializable {

}
