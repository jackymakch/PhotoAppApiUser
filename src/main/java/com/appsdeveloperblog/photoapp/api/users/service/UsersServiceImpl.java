package com.appsdeveloperblog.photoapp.api.users.service;

import com.appsdeveloperblog.photoapp.api.users.data.UsersEntity;
import com.appsdeveloperblog.photoapp.api.users.data.UsersRepository;
import com.appsdeveloperblog.photoapp.api.users.shared.UserDto;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.UUID;

@Service
public class UsersServiceImpl implements UsersService {

    UsersRepository usersRepository;
    BCryptPasswordEncoder bCryptPasswordEncoder;

    @Autowired
    public UsersServiceImpl(UsersRepository usersRepository, BCryptPasswordEncoder bCryptPasswordEncoder) {
        this.usersRepository = usersRepository;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
    }

    @Override
    public UserDto createUser(UserDto userDetails) {
        userDetails.setUserId(UUID.randomUUID().toString());
        userDetails.setEncryptedPassword(bCryptPasswordEncoder.encode(userDetails.getPassword()));
        ModelMapper modelMapper = new ModelMapper();
        modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);

        UsersEntity usersEntity = modelMapper.map(userDetails, UsersEntity.class);
        usersRepository.save(usersEntity);

        UserDto returnValue = modelMapper.map(usersEntity, UserDto.class);
        return returnValue;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        UsersEntity usersEntity = usersRepository.findByEmail(username);
        if (usersEntity == null) throw new UsernameNotFoundException(username);

        return new User(usersEntity.getEmail(), usersEntity.getEncryptedPassword(), true, true, true, true, new ArrayList<>());
    }
}
