package com.depromeet.breadmapbackend.domain.bakery.repository;

import com.depromeet.breadmapbackend.web.controller.bakery.dto.BakeryCardDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.util.List;

@Slf4j
@Repository
public class JdbcTemplateBakeryRepository {
    private final JdbcTemplate jdbcTemplate;

    public JdbcTemplateBakeryRepository(DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

//    public List<BakeryCardDto> getBakeryList(Double latitude, Double longitude, Double latitudeDelta, Double longitudeDelta) {
//        String sql = "select b.id as bakery_id, b.image as bakery_image, b.name as bakery_name," +
//                "b.flag_num as bakery_flagNum, b.rating as bakery_rating, b.review_num as bakery_reviewNum, " +
//                "acos(cos(radians(?))*cos(radians(b.latitude))*cos(radians(b.longitude)" +
//                "-radians(?))+sin(radians(?))*sin(radians(b.latitude)))*6371000 as bakery_distance\n" +
//                "from bakery b\n" +
//                "where (b.latitude between ?-? and ?+?) and (b.longitude between ?-? and ?+?)\n" +
//                "order by bakery_distance\n" +
//                "limit 20";
//
//        return jdbcTemplate.query(sql, bakeryCardDtoRowMapper(), latitude, longitude, latitude, latitude, latitudeDelta/2, latitude, latitudeDelta/2, longitude, longitudeDelta/2, longitude, longitudeDelta/2);
//    }
//
//
//    private RowMapper<BakeryCardDto> bakeryCardDtoRowMapper() {
//        return (rs, rowNum) -> BakeryCardDto.builder()
//                .id(rs.getLong("bakery_id"))
//                .image(rs.getString("bakery_image"))
//                .name(rs.getString("bakery_name"))
//                .flagNum(rs.getInt("bakery_flagNum"))
//                .rating(rs.getLong("bakery_rating"))
//                .reviewNum(rs.getInt("bakery_reviewNum"))
//                .distance(rs.getLong("bakery_distance")).build();
//    }

}
