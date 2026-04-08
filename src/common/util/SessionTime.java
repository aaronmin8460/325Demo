package common.util;

import java.time.Duration;

import java.time.LocalDateTime;

public class SessionTime {

    private LocalDateTime startTime;

    private LocalDateTime endTime;

    public SessionTime(LocalDateTime startTime, LocalDateTime endTime) {

        this.startTime = startTime;

        this.endTime = endTime;

    }

    public Duration getDuration() {

        // TODO: Calculate duration

        return Duration.between(startTime, endTime);

    }

    // getters and setters

    public LocalDateTime getStartTime() { return startTime; }

    public void setStartTime(LocalDateTime startTime) { this.startTime = startTime; }

    public LocalDateTime getEndTime() { return endTime; }

    public void setEndTime(LocalDateTime endTime) { this.endTime = endTime; }

}