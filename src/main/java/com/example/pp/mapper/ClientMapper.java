package com.example.pp.mapper;

import com.example.pp.entity.Client;
import com.example.pp.entity.ClientInfo;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ClientMapper {

    @Mapping(target = "fullName", expression = "java(getFullName(clientsInfo))")
    @Mapping(target = "birthDate", source = "birthday")
    @Mapping(target = "phone", source = "phone")
    @Mapping(target = "messageSend", constant = "false")
    Client toClient(ClientInfo clientsInfo);

    default String getFullName(ClientInfo info) {
        return info.getSurname() + " " +
                info.getName() + " " +
                info.getMiddleName();
    }
}
