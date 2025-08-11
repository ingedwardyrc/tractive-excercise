package com.tractive.pettracker.data.jpa;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface PetJpaRepository extends JpaRepository<PetEntity, Long> {
    @Query("select e.petType, e.trackerType, count(e) from PetEntity e where e.inZone = false group by e.petType, e.trackerType")
    List<Object[]> countOutsideZoneGrouped();
}
