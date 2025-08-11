package com.tractive.pettracker.data.jpa;

import com.tractive.pettracker.domain.PetType;
import com.tractive.pettracker.domain.TrackerType;
import jakarta.persistence.*;

@Entity
@Table(name = "pets")
public class PetEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(name = "pet_type", nullable = false)
    private PetType petType;

    @Enumerated(EnumType.STRING)
    @Column(name = "tracker_type", nullable = false)
    private TrackerType trackerType;

    @Column(name = "owner_id", nullable = false)
    private Integer ownerId;

    @Column(name = "in_zone", nullable = false)
    private Boolean inZone;

    @Column(name = "lost_tracker")
    private Boolean lostTracker;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public PetType getPetType() { return petType; }
    public void setPetType(PetType petType) { this.petType = petType; }
    public TrackerType getTrackerType() { return trackerType; }
    public void setTrackerType(TrackerType trackerType) { this.trackerType = trackerType; }
    public Integer getOwnerId() { return ownerId; }
    public void setOwnerId(Integer ownerId) { this.ownerId = ownerId; }
    public Boolean getInZone() { return inZone; }
    public void setInZone(Boolean inZone) { this.inZone = inZone; }
    public Boolean getLostTracker() { return lostTracker; }
    public void setLostTracker(Boolean lostTracker) { this.lostTracker = lostTracker; }
}
