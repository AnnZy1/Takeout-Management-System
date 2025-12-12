package com.delivery.management.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.delivery.management.entity.Dish;
import org.apache.ibatis.annotations.*;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 菜品Mapper接口
 * 
 * @author system
 * @date 2025-01-15
 */
@Mapper
public interface DishMapper extends BaseMapper<Dish> {

    /**
     * 查询包含已删除的菜品列表（绕过MyBatis-Plus的自动软删除过滤）
     * 
     * @param sql 完整的SQL查询语句
     * @return 菜品列表
     */
    @Select("${sql}")
    List<Dish> selectListByCustomSql(@Param("sql") String sql);

    /**
     * 根据自定义SQL查询总记录数
     * 
     * @param sql 完整的SQL查询语句
     * @return 总记录数
     */
    @Select("${sql}")
    Long selectCountByCustomSql(@Param("sql") String sql);

    /**
     * 根据ID查询菜品（忽略逻辑删除状态）
     * 
     * @param id 菜品ID
     * @return 菜品信息
     */
    @Select("SELECT * FROM dish WHERE id = #{id}")
    Dish selectByIdIgnoreDeleted(@Param("id") Long id);

    /**
     * 恢复菜品（将is_deleted设置为0）
     * 
     * @param id 菜品ID
     * @param updateUser 更新人ID
     * @param updateTime 更新时间
     * @return 更新结果
     */
    @Update("UPDATE dish SET is_deleted = 0, update_time = #{updateTime}, update_user = #{updateUser} WHERE id = #{id}")
    int restoreDish(@Param("id") Long id, @Param("updateUser") Long updateUser, @Param("updateTime") LocalDateTime updateTime);

    /**
     * 物理删除菜品（绕过MyBatis-Plus的逻辑删除过滤）
     * 
     * @param id 菜品ID
     * @return 删除结果
     */
    @Delete("DELETE FROM dish WHERE id = #{id}")
    int deletePermanently(@Param("id") Long id);
}

