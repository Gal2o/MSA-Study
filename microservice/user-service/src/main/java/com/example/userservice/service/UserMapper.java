package com.example.userservice.service;

import com.example.userservice.dto.UserDto;
import com.example.userservice.jpa.UserEntity;
import com.example.userservice.vo.RequestUser;
import com.example.userservice.vo.ResponseUser;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface UserMapper {
    UserMapper instance = Mappers.getMapper(UserMapper.class);

    UserEntity dtoToEntity(UserDto userDto);
    UserDto userToDto(RequestUser user);
    UserDto entityToDto(UserEntity userEntity);
    ResponseUser dtoToResponseUser(UserDto userDto);
    ResponseUser entityToResponseUser(UserEntity userEntity);
}
