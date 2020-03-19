/*
 * Copyright © Marc Auberer 2019 - 2020. All rights reserved
 */

package com.chillibits.particulatematterapi.model.db.main;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class User {
    // Constants
    public static final int ACTIVE = 1;
    public static final int EMAIL_CONFIRMATION_PENDING = 2;
    public static final int SUSPENDED = 3;
    public static final int LOCKED = 4;

    public static final int USER = 1;
    public static final int OPERATOR = 2;
    public static final int ADMINISTRATOR = 3;

    // Attributes
    @Id
    private int id;
    private String firstName;
    private String lastName;
    private String email;
    private String password;
    private int role;
    private int status;
    private long creationTimestamp;
    private long lastEditTimestamp;
}