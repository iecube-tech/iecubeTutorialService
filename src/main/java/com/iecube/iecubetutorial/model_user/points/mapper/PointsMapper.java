package com.iecube.iecubetutorial.model_user.points.mapper;

import com.iecube.iecubetutorial.model_user.points.entity.Points;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface PointsMapper {
    int createPoints(Points points);

    int updatePoints(Points points);

    Points findValidPointsByOSecId(Long oSecId);

    List<Points> findAllPointsByOSecId(Long oSecId);
}
