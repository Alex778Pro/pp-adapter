package com.example.pp.mapper;

import com.example.pp.entity.Client;
import com.example.pp.entity.ClientsInfo;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface ClientMapper {
    ClientMapper INSTANCE = Mappers.getMapper(ClientMapper.class);

    @Mapping(target = "fullName", expression = "java(getFullName(clientsInfo))")
    @Mapping(target = "birthDate", source = "birthday")
    @Mapping(target = "phone", source = "phone")
    @Mapping(target = "messageSend", constant = "false")
    Client toClient(ClientsInfo clientsInfo);

    default String getFullName(ClientsInfo info) {
        return info.getSurname() + " " +
                info.getName() + " " +
                info.getMiddleName();
    }
}
