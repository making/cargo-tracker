package se.citerus.dddsample.interfaces.booking.web;

import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class RouteAssignmentCommand {

	private String trackingId;

	private List<LegCommand> legs = new ArrayList<>();

	public String getTrackingId() {
		return trackingId;
	}

	public void setTrackingId(String trackingId) {
		this.trackingId = trackingId;
	}

	public List<LegCommand> getLegs() {
		return legs;
	}

	public void setLegs(List<LegCommand> legs) {
		this.legs = legs;
	}

	public static final class LegCommand {

		private String voyageNumber;

		private String fromUnLocode;

		private String toUnLocode;

		private LocalDateTime fromDate;

		private LocalDateTime toDate;

		public String getVoyageNumber() {
			return voyageNumber;
		}

		public void setVoyageNumber(final String voyageNumber) {
			this.voyageNumber = voyageNumber;
		}

		public String getFromUnLocode() {
			return fromUnLocode;
		}

		public void setFromUnLocode(final String fromUnLocode) {
			this.fromUnLocode = fromUnLocode;
		}

		public String getToUnLocode() {
			return toUnLocode;
		}

		public void setToUnLocode(final String toUnLocode) {
			this.toUnLocode = toUnLocode;
		}

		public LocalDateTime getFromDate() {
			return fromDate;
		}

		@DateTimeFormat(pattern = "uuuu-MM-dd HH:mm")
		public void setFromDate(LocalDateTime fromDate) {
			this.fromDate = fromDate;
		}

		public LocalDateTime getToDate() {
			return toDate;
		}

		@DateTimeFormat(pattern = "uuuu-MM-dd HH:mm")
		public void setToDate(LocalDateTime toDate) {
			this.toDate = toDate;
		}

	}

}
