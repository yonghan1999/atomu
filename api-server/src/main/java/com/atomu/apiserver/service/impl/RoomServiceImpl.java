package com.atomu.apiserver.service.impl;

import com.atomu.apiserver.entity.Meeting;
import com.atomu.apiserver.entity.Msgserver;
import com.atomu.apiserver.mapper.MeetingserverMapper;
import com.atomu.apiserver.mapper.MsgserverMapper;
import com.atomu.apiserver.service.MeetingService;
import com.atomu.apiserver.service.RoomService;
import com.atomu.apiserver.util.ErrorCode;
import com.atomu.apiserver.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Service
public class RoomServiceImpl implements RoomService {
    @Autowired
    MeetingService meetingService;

    @Autowired
    MeetingserverMapper meetingserverMapper;
    @Autowired
    MsgserverMapper msgserverMapper;

    @Override
    public Map<String, Object> enterRoom(Meeting meeting) {
        Map<String,Object> res = new HashMap<>();
        if(meeting==null || meeting.getUid()==null || meeting.getId()==null || meeting.getCode()==null) {
            res.put("code",ErrorCode.UNABLE_TO_PARSE_SUBMITTED_DATA);
            return res;
        }
        Meeting searched = meetingService.searchMeetingById(meeting);
        if(searched==null || !searched.getCode().equals(meeting.getCode())) {
            res.put("code",ErrorCode.NO_MEETING);
            return res;
        }
        Date date = new Date(System.currentTimeMillis());
        if(date.after(searched.getStart())) {
            res.put("code", ErrorCode.MEETING_IS_NOT_START);
            res.put("errResult",searched);
            return res;
        }
        if(searched.getRealend()!=null){
            res.put("code", ErrorCode.MEETING_IS_ENDED);
            res.put("errResult",searched);
            return res;
        }

        res.put("meeting",searched);
        Map<String,String> map = new HashMap<>();
        map.put("uid",meeting.getUid().toString());
        map.put("mid",searched.getId().toString());
        String token = JwtUtil.genToken(map);
        res.put("token",token);
        Msgserver msgserver = msgserverMapper.selectByPrimaryKey(meetingserverMapper.selectByMid(searched.getId()).getSid());
        res.put("msgserver",msgserver);
        return res;
    }
}
