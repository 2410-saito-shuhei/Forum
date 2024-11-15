package com.example.forum.mapper;

import com.example.forum.repository.entity.Report;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;

@Mapper
public interface ReportMapper {
    List<Report> findByCreatedDateBetweenOrderByUpdatedDateDesc(@Param("start") Date startDate,
                                                                @Param("end") Date endDate);
}
