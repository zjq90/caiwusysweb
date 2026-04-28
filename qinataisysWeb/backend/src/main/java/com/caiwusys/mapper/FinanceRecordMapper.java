package com.caiwusys.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.caiwusys.entity.FinanceRecord;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@Mapper
public interface FinanceRecordMapper extends BaseMapper<FinanceRecord> {

    @Select("SELECT c.name, SUM(r.amount) as total FROM finance_records r " +
            "LEFT JOIN categories c ON r.category_id = c.id " +
            "WHERE r.user_id = #{userId} AND r.type = #{type} " +
            "AND YEAR(r.record_time) = #{year} AND MONTH(r.record_time) = #{month} " +
            "AND r.deleted = 0 " +
            "GROUP BY r.category_id, c.name " +
            "ORDER BY total DESC")
    List<Map<String, Object>> getCategoryStatistics(@Param("userId") Long userId, 
                                                      @Param("type") Integer type, 
                                                      @Param("year") Integer year,
                                                      @Param("month") Integer month);

    @Select("SELECT SUM(amount) FROM finance_records " +
            "WHERE user_id = #{userId} AND type = #{type} " +
            "AND YEAR(record_time) = #{year} AND MONTH(record_time) = #{month} " +
            "AND deleted = 0")
    BigDecimal getMonthlyTotal(@Param("userId") Long userId, 
                                @Param("type") Integer type, 
                                @Param("year") Integer year,
                                @Param("month") Integer month);

    @Select("SELECT DAY(r.record_time) as day, " +
            "SUM(CASE WHEN r.type = 1 THEN r.amount ELSE 0 END) as income, " +
            "SUM(CASE WHEN r.type = 2 THEN r.amount ELSE 0 END) as expense " +
            "FROM finance_records r " +
            "WHERE r.user_id = #{userId} " +
            "AND YEAR(r.record_time) = #{year} AND MONTH(r.record_time) = #{month} " +
            "AND r.deleted = 0 " +
            "GROUP BY DAY(r.record_time) " +
            "ORDER BY day")
    List<Map<String, Object>> getDailyStatistics(@Param("userId") Long userId, 
                                                   @Param("year") Integer year,
                                                   @Param("month") Integer month);

    @Select("SELECT MONTH(r.record_time) as month, " +
            "SUM(CASE WHEN r.type = 1 THEN r.amount ELSE 0 END) as income, " +
            "SUM(CASE WHEN r.type = 2 THEN r.amount ELSE 0 END) as expense " +
            "FROM finance_records r " +
            "WHERE r.user_id = #{userId} " +
            "AND YEAR(r.record_time) = #{year} " +
            "AND r.deleted = 0 " +
            "GROUP BY MONTH(r.record_time) " +
            "ORDER BY month")
    List<Map<String, Object>> getMonthlyStatistics(@Param("userId") Long userId, 
                                                     @Param("year") Integer year);

    @Select("SELECT SUM(amount) FROM finance_records " +
            "WHERE user_id = #{userId} AND type = 2 " +
            "AND category_id = #{categoryId} " +
            "AND YEAR(record_time) = #{year} AND MONTH(record_time) = #{month} " +
            "AND deleted = 0")
    BigDecimal getCategoryExpense(@Param("userId") Long userId, 
                                   @Param("categoryId") Long categoryId, 
                                   @Param("year") Integer year,
                                   @Param("month") Integer month);

    @Select("SELECT c.name, SUM(r.amount) as amount FROM finance_records r " +
            "LEFT JOIN categories c ON r.category_id = c.id " +
            "WHERE r.user_id = #{userId} AND r.type = 2 " +
            "AND YEAR(r.record_time) = #{year} AND MONTH(r.record_time) = #{month} " +
            "AND r.deleted = 0 " +
            "GROUP BY r.category_id, c.name " +
            "ORDER BY amount DESC")
    List<Map<String, Object>> getExpenseByCategory(@Param("userId") Long userId, 
                                                     @Param("year") Integer year,
                                                     @Param("month") Integer month);
}
