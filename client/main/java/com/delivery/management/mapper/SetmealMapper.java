package com.delivery.management.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.delivery.management.entity.Setmeal;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.apache.ibatis.annotations.Delete;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 套餐Mapper接口
 * 
 * @author system
 * @date 2025-01-15
 */
@Mapper
public interface SetmealMapper extends BaseMapper<Setmeal> {

    /**
     * 根据ID查询套餐（忽略逻辑删除状态）
     * 
     * @param id 套餐ID
     * @return 套餐信息
     */
    @Select("SELECT * FROM setmeal WHERE id = #{id}")
    Setmeal selectByIdIgnoreDeleted(@Param("id") Long id);

    /**
     * 恢复套餐（将is_deleted设置为0）
     * 
     * @param id 套餐ID
     * @param updateUser 更新人ID
     * @param updateTime 更新时间
     * @return 更新结果
     */
    @Update("UPDATE setmeal SET is_deleted = 0, update_time = #{updateTime}, update_user = #{updateUser} WHERE id = #{id}")
    int restoreSetmeal(@Param("id") Long id, @Param("updateUser") Long updateUser, @Param("updateTime") LocalDateTime updateTime);

    /**
     * 物理删除套餐（绕过MyBatis-Plus的逻辑删除过滤）
     * 
     * @param id 套餐ID
     * @return 删除结果
     */
    @Delete("DELETE FROM setmeal WHERE id = #{id}")
    int deletePermanently(@Param("id") Long id);
}

