package com.kanflow.service;

import com.kanflow.api.dto.SprintDtos.SprintCreateRequest;
import com.kanflow.api.dto.SprintDtos.SprintResponse;
import com.kanflow.api.dto.SprintDtos.SprintUpdateRequest;
import com.kanflow.api.error.ResourceNotFoundException;
import com.kanflow.domain.entity.Sprint;
import com.kanflow.repository.SprintRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class SprintService {

    private final SprintRepository sprintRepository;

    @Transactional(readOnly = true)
    public List<SprintResponse> listar() {
        return sprintRepository.findAll().stream().map(this::toResponse).toList();
    }

    @Transactional(readOnly = true)
    public SprintResponse buscar(UUID id) {
        return toResponse(sprintRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Sprint não encontrada: " + id)));
    }

    @Transactional
    public SprintResponse criar(SprintCreateRequest req) {
        Sprint s = new Sprint();
        s.setNome(req.nome());
        s.setDataInicio(req.dataInicio());
        s.setDataFim(req.dataFim());
        s.setStatus(req.status());
        return toResponse(sprintRepository.save(s));
    }

    @Transactional
    public SprintResponse atualizar(UUID id, SprintUpdateRequest req) {
        Sprint s = sprintRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Sprint não encontrada: " + id));
        s.setNome(req.nome());
        s.setDataInicio(req.dataInicio());
        s.setDataFim(req.dataFim());
        s.setStatus(req.status());
        return toResponse(sprintRepository.save(s));
    }

    @Transactional
    public void excluir(UUID id) {
        if (!sprintRepository.existsById(id)) {
            throw new ResourceNotFoundException("Sprint não encontrada: " + id);
        }
        sprintRepository.deleteById(id);
    }

    @Transactional(readOnly = true)
    public Sprint getReference(UUID id) {
        return sprintRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Sprint não encontrada: " + id));
    }

    private SprintResponse toResponse(Sprint s) {
        return new SprintResponse(s.getId(), s.getNome(), s.getDataInicio(), s.getDataFim(), s.getStatus());
    }
}
