package com.tractive.pettracker.domain;

public class Cat extends Pet {
    private Boolean lostTracker;

    public Cat(Long id, TrackerType trackerType, Integer ownerId, Boolean inZone, Boolean lostTracker) {
        super(id, PetType.CAT, trackerType, ownerId, inZone);
        this.lostTracker = lostTracker;
    }

    public Boolean getLostTracker() { return lostTracker; }
}
