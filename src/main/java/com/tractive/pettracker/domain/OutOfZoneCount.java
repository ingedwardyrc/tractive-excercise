package com.tractive.pettracker.domain;

public class OutOfZoneCount {
    private final PetType petType;
    private final TrackerType trackerType;
    private final int count;

    public OutOfZoneCount(PetType petType, TrackerType trackerType, int count) {
        this.petType = petType;
        this.trackerType = trackerType;
        this.count = count;
    }

    public PetType getPetType() { return petType; }
    public TrackerType getTrackerType() { return trackerType; }
    public int getCount() { return count; }
}
