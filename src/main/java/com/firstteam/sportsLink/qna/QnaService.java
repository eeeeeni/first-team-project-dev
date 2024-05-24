package com.firstteam.sportsLink.qna;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class QnaService {

    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public QnaService(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public void saveQna(QnaDTO qnaDTO) {
        // qnaDTO를 이용하여 DB에 저장하는 로직을 구현
        LocalDate date = qnaDTO.getDate();
        if (date == null) {
            date = LocalDate.now(); // 현재 날짜로 설정
        }
        String sql = "INSERT INTO inquiries (title, author, date, content, hit) VALUES (?, ?, ?, ?, ?)";
        jdbcTemplate.update(sql, qnaDTO.getTitle(), qnaDTO.getAuthor(), date, qnaDTO.getContent(), 0);
    }

    public void increaseHit(Long id) {
        String sql = "UPDATE inquiries SET hit = hit + 1 WHERE id = ?";
        jdbcTemplate.update(sql, id);
    }

    // 데이터베이스에서 모든 문의사항을 가져오는 메서드
    public List<QnaDTO> getAllInquiries() {
        // SQL 쿼리를 작성합니다.
        String sql = "SELECT * FROM inquiries";
        // 쿼리를 실행하여 결과를 QnaDTO 객체로 매핑하여 리스트로 반환합니다.
        return jdbcTemplate.query(sql, new BeanPropertyRowMapper<>(QnaDTO.class));
    }

    public QnaDTO getInquiryById(Long id) {
        String sql = "SELECT * FROM inquiries WHERE id = ?";
        try {
            return jdbcTemplate.queryForObject(sql, new Object[]{id}, new BeanPropertyRowMapper<>(QnaDTO.class));
        } catch (EmptyResultDataAccessException e) {
            return null; // 결과가 없을 경우 null 반환
        }
    }

}