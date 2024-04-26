package tech.ada.java.agendamentoconsultas.controllers;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import tech.ada.java.agendamentoconsultas.model.Dto.AppointmentRequestDto;
import tech.ada.java.agendamentoconsultas.model.Dto.AppointmentResponseDto;
import tech.ada.java.agendamentoconsultas.model.Dto.AppointmentDeleteRequestDto;
import tech.ada.java.agendamentoconsultas.service.AppointmentService;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1")
public class AppointmentController {

    private final AppointmentService appointmentService;

    @GetMapping ("/patients/{patientUuid}/appointments")
    public List<AppointmentResponseDto> findAllByPatientUuid(@PathVariable UUID patientUuid) {
        return appointmentService.findAllByPatientUuid(patientUuid);
    }

    @GetMapping ("/doctors/{doctorUuid}/appointments")
    public List<AppointmentResponseDto> findAllByDoctorUuid(@RequestParam("exact-date") Optional<LocalDate> date, @PathVariable UUID doctorUuid) {
        if(date.isPresent()) {
         return appointmentService.findAllByDoctorUuidAndAppointmentDate(doctorUuid, date.get());
        }
        return appointmentService.findAllByDoctorUuid(doctorUuid);
    }

    @PostMapping("/patients/{patientUuid}/doctors/{doctorUuid}")
    public AppointmentResponseDto create(@RequestBody @Valid AppointmentRequestDto request, @PathVariable UUID doctorUuid, @PathVariable UUID patientUuid) {
        return appointmentService.create(request, doctorUuid, patientUuid);
    }

    @PutMapping("/patients/{patientUuid}/doctors/{doctorUuid}")
    public void update(@RequestBody @Valid AppointmentRequestDto request, @PathVariable UUID patientUuid, @PathVariable UUID doctorUuid) {
        appointmentService.update(request, patientUuid, doctorUuid);
    }

    @DeleteMapping("/doctors/{doctorUuid}/patients/{patientUuid}")
    public void update(@RequestBody @Valid AppointmentDeleteRequestDto request, @PathVariable UUID patientUuid, @PathVariable UUID doctorUuid) {
        appointmentService.delete(request, patientUuid, doctorUuid);
    }
}
