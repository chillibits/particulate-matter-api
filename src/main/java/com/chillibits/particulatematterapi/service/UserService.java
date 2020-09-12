/*
 * Copyright © Marc Auberer 2019 - 2020. All rights reserved
 */

package com.chillibits.particulatematterapi.service;

import com.chillibits.particulatematterapi.exception.ErrorCode;
import com.chillibits.particulatematterapi.exception.exception.UserDataException;
import com.chillibits.particulatematterapi.model.db.main.User;
import com.chillibits.particulatematterapi.model.dto.UserDto;
import com.chillibits.particulatematterapi.model.dto.UserInsertUpdateDto;
import com.chillibits.particulatematterapi.repository.UserRepository;
import com.chillibits.particulatematterapi.shared.SharedUtils;
import org.apache.commons.codec.digest.DigestUtils;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ModelMapper mapper;
    @Autowired
    private JavaMailSender mailer;

    public List<UserDto> getAllUsers() {
        return userRepository.findAll().stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    public UserDto getUserByEmail(String email) {
        if(email == null) return null; // Return null if email is null
        return convertToDto(userRepository.findByEmail(email));
    }

    public UserDto checkUserDataAndSignIn(String email, String password) throws UserDataException {
        if(email == null || password == null) return null; // Return null if one of email or password is null

        User user = userRepository.findByEmail(email);
        if(user == null) throw new UserDataException(ErrorCode.USER_NOT_EXISTING);
        if(!DigestUtils.sha256Hex(user.getPassword()).equals(password) && !user.getPassword().equals(password))
            throw new UserDataException(ErrorCode.PASSWORD_WRONG);

        return convertToDto(user);
    }

    public UserDto addUser(UserInsertUpdateDto user) throws UserDataException {
        // Validity checks
        validateUserObject(user);
        if(userRepository.findByEmail(user.getEmail()) != null) throw new UserDataException(ErrorCode.USER_ALREADY_EXISTS);
        // Send confirmation email with confirmation token
        String confirmationToken = sendConfirmationEmail(user.getEmail(), user.getLastName());
        // Build UserDbo object
        User userDbo = convertToDbo(user);
        long currentTimestamp = System.currentTimeMillis();
        userDbo.setCreationTimestamp(currentTimestamp);
        userDbo.setLastEditTimestamp(currentTimestamp);
        userDbo.setStatus(User.EMAIL_CONFIRMATION_PENDING);
        userDbo.setRole(User.USER);
        userDbo.setConfirmationToken(confirmationToken);
        return convertToDto(userRepository.save(userDbo));
    }

    public Boolean confirmAccount(String confirmationToken) {
        // Search after user item with the passed confirmation token
        User user = userRepository.findByConfirmationToken(confirmationToken);
        if(user == null || user.getStatus() != User.EMAIL_CONFIRMATION_PENDING) return false;
        // Found. Update its status
        user.setStatus(User.ACTIVE);
        userRepository.updateUser(user);
        return true;
    }

    public Integer updateUser(UserInsertUpdateDto user) throws UserDataException {
        // Validity checks
        validateUserObject(user);
        if(userRepository.findByEmail(user.getEmail()) == null) throw new UserDataException(ErrorCode.USER_NOT_EXISTING);
        return userRepository.updateUser(convertToDbo(user));
    }

    public void deleteUserById(Integer id) {
        userRepository.deleteById(id);
    }

    // ---------------------------------------------- Utility functions ------------------------------------------------

    private String sendConfirmationEmail(String email, String lastName) {
        // Generate confirmation token
        String confirmationToken = SharedUtils.generateRandomString(20);

        // Generate email text
        String salutation = lastName.isEmpty() ? "Dear app user" : "Dear Mr./Mrs. " + lastName;
        String confirmationUrl = "https://api.pm.chillibits.com/user/confirm?confirmationToken=" + confirmationToken;
        String text = salutation + ",\r\nThank you for downloading the Particulate Matter App.\r\nWe send you this email to verify, " +
                "that you have control over this email address. If you got this mail mistakenly, please ignore it. Otherwise, please " +
                "click on the button below, to activate your user account and be able to sign in.\r\n\r\n" + confirmationUrl + "\r\n" +
                "\r\nBest regards,\r\nYour ChilliBits Team";

        // Send email
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom("noreply@pm.chillibits.com");
        message.setTo(email);
        message.setSubject("Particulate Matter App - Account confirmation");
        message.setText(text);
        mailer.send(message);

        return confirmationToken;
    }

    private void validateUserObject(UserInsertUpdateDto user) throws UserDataException {
        if(user.getEmail().isBlank() || user.getPassword().isBlank()) throw new UserDataException(ErrorCode.INVALID_USER_DATA);
    }

    private UserDto convertToDto(User user) {
        return mapper.map(user, UserDto.class);
    }

    private User convertToDbo(UserInsertUpdateDto user) {
        return mapper.map(user, User.class);
    }
}