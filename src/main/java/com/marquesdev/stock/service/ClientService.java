package com.marquesdev.stock.service;

import com.marquesdev.stock.dto.ClientRequestDto;
import com.marquesdev.stock.dto.ClientResponseDto;
import com.marquesdev.stock.entity.Client;
import com.marquesdev.stock.exception.CnpjUniqueException;
import com.marquesdev.stock.exception.EmailUniqueException;
import com.marquesdev.stock.mapper.ClientMapper;
import com.marquesdev.stock.repository.ClientRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ClientService {

    private final ClientRepository clientRepository;
    private final ClientMapper clientMapper;

    @Transactional
    public ClientResponseDto saveClient(ClientRequestDto clientRequestDto){

        if (clientRepository.existsByEmail(clientRequestDto.email())){
            throw new EmailUniqueException("Email Already exists.");
        }

        if (clientRepository.existsByCnpj(clientRequestDto.cnpj())){
            throw new CnpjUniqueException("CNPJ Already exists.");
        }

        Client client = clientMapper.toClient(clientRequestDto);
        Client savedClient = clientRepository.save(client);
        return clientMapper.toDto(savedClient);

    }

}
