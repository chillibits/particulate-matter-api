package com.chillibits.particulatematterapi.model.io;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "customSequences")
@Data
public class CustomSequence {
    @Id
    private String id;
    private int seq;
}