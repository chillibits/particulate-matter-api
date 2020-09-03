/*
 * Copyright © Marc Auberer 2019 - 2020. All rights reserved
 */

package com.chillibits.particulatematterapi.service;

import com.chillibits.particulatematterapi.exception.ErrorCode;
import com.chillibits.particulatematterapi.exception.exception.LogAccessException;
import com.chillibits.particulatematterapi.model.db.data.LogItem;
import com.chillibits.particulatematterapi.model.dto.LogItemDto;
import com.chillibits.particulatematterapi.shared.ConstantUtils;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class LogService {

    @Autowired
    private MongoTemplate template;
    @Autowired
    private ModelMapper mapper;

    public List<LogItemDto> getAllLogs(long from, long to) throws LogAccessException {
        return template.find(Query.query(getTimeCriteria(from, to)), LogItem.class).stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    public List<LogItemDto> getLogsByTarget(String target, long from, long to) throws LogAccessException {
        return template.find(Query.query(getTimeCriteria(from, to).and("target").regex(target)), LogItem.class).stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    public List<LogItemDto> getLogsByUser(int userId, long from, long to) throws LogAccessException {
        return template.find(Query.query(getTimeCriteria(from, to).and("userId").is(userId)), LogItem.class).stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    public List<LogItemDto> getLogsByClient(int clientId, long from, long to) throws LogAccessException {
        return template.find(Query.query(getTimeCriteria(from, to).and("clientId").is(clientId)), LogItem.class).stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    public List<LogItemDto> getLogsByAction(String action, long from, long to) throws LogAccessException {
        return template.find(Query.query(getTimeCriteria(from, to).and("action").regex(action)), LogItem.class).stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    // ---------------------------------------------- Utility functions ------------------------------------------------

    private Criteria getTimeCriteria(long from, long to) throws LogAccessException {
        validateRequest(from, to);

        long toTimestamp = to == 0 ? System.currentTimeMillis() : to;
        long fromTimestamp = from == 0 ? toTimestamp - ConstantUtils.DEFAULT_DATA_TIME_SPAN : from;
        return Criteria.where("timestamp").gte(fromTimestamp).lte(toTimestamp);
    }

    private LogItemDto convertToDto(LogItem logItem) {
        return mapper.map(logItem, LogItemDto.class);
    }

    private void validateRequest(long from, long to) throws LogAccessException {
        if (from < 0 || to < 0 || from > to) throw new LogAccessException(ErrorCode.INVALID_TIME_RANGE_LOG);
    }
}