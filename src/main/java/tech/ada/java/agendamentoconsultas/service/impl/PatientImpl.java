package tech.ada.java.agendamentoconsultas.service.impl;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import org.modelmapper.ModelMapper;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import tech.ada.java.agendamentoconsultas.exception.CepNotFoundException;
import tech.ada.java.agendamentoconsultas.exception.PatientNotFoundException;
import tech.ada.java.agendamentoconsultas.model.Address;
import tech.ada.java.agendamentoconsultas.model.Dto.PatientUpdateRequestDto;
import tech.ada.java.agendamentoconsultas.model.Patient;
import tech.ada.java.agendamentoconsultas.model.Dto.PatientDtoRequest;
import tech.ada.java.agendamentoconsultas.model.Dto.PatientDtoResponse;
import tech.ada.java.agendamentoconsultas.repository.AddressRepository;
import tech.ada.java.agendamentoconsultas.repository.PatientRepository;
import tech.ada.java.agendamentoconsultas.service.PatientService;
import tech.ada.java.agendamentoconsultas.service.ViaCepService;
import tech.ada.java.agendamentoconsultas.utils.CepUtils;
import tech.ada.java.agendamentoconsultas.utils.DocumentUtils;

@Service
public class PatientImpl implements PatientService{

    private final PatientRepository patientRepository;
    private final ViaCepService viaCepService;
    private final AddressRepository addressRepository;
    private final ModelMapper modelMapper;

    public PatientImpl(PatientRepository patientRepository, ModelMapper modelMapper,ViaCepService viaCepService, AddressRepository addressRepository){
        this.patientRepository = patientRepository;
        this.modelMapper = modelMapper;
        this.addressRepository = addressRepository;
        this.viaCepService = viaCepService;
    }

    @Override
    public PatientDtoResponse createPatient(PatientDtoRequest request) {
        
        Optional<Address> optionalAddress = addressRepository.findByCep(CepUtils.removeNotNumberCharToCep(request.getAddressRequestDto().getCep()));
        Address address = new Address();

        try {
            if(optionalAddress.isPresent()){
                address = optionalAddress.get();
            }else{
                address = modelMapper.map(viaCepService.findAddressByCep(request.getAddressRequestDto().getCep()), Address.class);
                address.setCep(CepUtils.removeNotNumberCharToCep(address.getCep()));
                addressRepository.save(address);
            }
        } catch (RuntimeException e) {
            throw new CepNotFoundException(request.getAddressRequestDto().getCep());
        }

        String encryptedPassword = new BCryptPasswordEncoder().encode(request.getSenha());

        Patient patient = new Patient(request.getNome(), request.getEmail(), encryptedPassword, request.getTelefone(), request.getCpf(), address);
        patientRepository.save(patient);
        
        return new PatientDtoResponse(request.getNome(), request.getEmail(), patient.getUuid());
    }

    @Override
    public void update(PatientUpdateRequestDto request, UUID uuid) {
        
        Patient paciente = patientRepository.findByUuid(uuid).orElseThrow(PatientNotFoundException::new);

        paciente.setTelefone(request.getTelefone());
        paciente.setNome(request.getNome());
        paciente.setEmail(request.getEmail());
        paciente.setUpdateAt(LocalDateTime.now());

        patientRepository.save(paciente);
    }

    @Override
    public void deletePatient(UUID uuid) {
        Patient paciente = patientRepository.findByUuid(uuid).orElseThrow(PatientNotFoundException::new);    
        paciente.setIsActive(false);
        paciente.setUpdateAt(LocalDateTime.now());
        patientRepository.save(paciente);
    }
}
