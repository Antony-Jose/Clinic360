package com.clinic360.clinic360.repository;

import com.clinic360.clinic360.entity.Doctor;
import com.clinic360.clinic360.entity.TimeSlot;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.List;

@Repository
public interface TimeSlotRepository extends JpaRepository<TimeSlot, Long> {
    List<TimeSlot> findByDoctorAndDayOfWeek(Doctor doctor, DayOfWeek dayOfWeek);
    
    List<TimeSlot> findByDoctorAndDayOfWeekAndAvailableTrue(Doctor doctor, DayOfWeek dayOfWeek);
    
    @Query("SELECT t FROM TimeSlot t WHERE t.doctor.id = :doctorId AND t.dayOfWeek = :dayOfWeek " +
           "AND t.startTime >= :startTime AND t.endTime <= :endTime")
    List<TimeSlot> findByDoctorIdAndDayOfWeekAndTimeRange(
            Long doctorId, DayOfWeek dayOfWeek, LocalTime startTime, LocalTime endTime);
} 