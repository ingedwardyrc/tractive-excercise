package com.tractive.pettracker.domain;

// This should be a record but the requirements asked for this inheritance
public class Pet {
    private Long id;
    private PetType petType;
    private TrackerType trackerType;
    private Integer ownerId;
    private Boolean inZone;

    public Pet(Long id, PetType petType, TrackerType trackerType, Integer ownerId, Boolean inZone) {
        this.id = id;
        this.petType = petType;
        this.trackerType = trackerType;
        this.ownerId = ownerId;
        this.inZone = inZone;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public PetType getPetType() { return petType; }

    public TrackerType getTrackerType() { return trackerType; }

    public Integer getOwnerId() { return ownerId; }

    public Boolean getInZone() { return inZone; }
}
