package tech.ada.java.agendamentoconsultas.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import tech.ada.java.agendamentoconsultas.model.Appointment;
import tech.ada.java.agendamentoconsultas.model.Doctor;
import tech.ada.java.agendamentoconsultas.model.Patient;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface AppointmentRepository extends JpaRepository<Appointment, Long> {
    @Query("""
            select (count(a) > 0) from Appointment a
            where a.appointmentDate = :appointmentDate and a.doctor.uuid = :doctorUuid and :startTime between a.appointmentStartTime and a.appointmentEndTime""")
    boolean appointmentExists(@Param("appointmentDate") LocalDate appointmentDate,
                              @Param("doctorUuid") UUID doctorUuid,
                              @Param("startTime") LocalTime appointmentStartTimeStart);


    List<Appointment> findAllByPatient(Patient patient);

    Optional<Appointment> findByDoctorAndUuid(Doctor doctor, UUID uuid);

    List<Appointment> findAllByDoctorUuid(UUID doctorUuid);

    List<Appointment> findAllByDoctorUuidAndAppointmentDate(UUID doctorUuid, LocalDate date);
}
