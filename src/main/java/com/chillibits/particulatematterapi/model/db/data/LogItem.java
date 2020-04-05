/*
 * Copyright © Marc Auberer 2019 - 2020. All rights reserved
 */

package com.chillibits.particulatematterapi.model.db.data;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Document
@NoArgsConstructor
@AllArgsConstructor
public class LogItem {
    // Actions
    public static final String ACTION_PUSH = "Push";

    // Attributes
    private long timestamp;
    private int clientId;
    private int userId;
    private String action;
    private String target;
}