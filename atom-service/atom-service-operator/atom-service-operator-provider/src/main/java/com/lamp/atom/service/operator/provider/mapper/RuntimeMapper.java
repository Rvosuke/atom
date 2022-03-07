/*
 *Copyright (c) [Year] [name of copyright holder]
 *[Software Name] is licensed under Mulan PubL v2.
 *You can use this software according to the terms and conditions of the Mulan PubL v2.
 *You may obtain a copy of Mulan PubL v2 at:
 *         http://license.coscl.org.cn/MulanPubL-2.0
 *THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND,
 *EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT,
 *MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 *See the Mulan PubL v2 for more details.
 */
package com.lamp.atom.service.operator.provider.mapper;

import com.lamp.atom.service.operator.entity.RuntimeEntity;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

@Mapper
public interface RuntimeMapper {
    /**
     * 添加实例
     * @param runtimeEntity
     */
    @Insert("insert into runtime" +
            "(space_id,case_source_type,source_id,node_id," +
            "server_ip,server_port,start_time,end_time,estimate_start_time,estimate_end_time," +
            "runtime_status,label,start_id,start_name,end_id,end_name) " +
            "values(#{spaceId},#{caseSourceType},#{sourceId},#{nodeId}," +
            "#{serverIp},#{serverPort},#{startTime},#{endTime},#{estimateStartTime},#{estimateEndTime}," +
            "#{runtimeStatus},#{label},#{startId},#{startName},#{endId},#{endName})")
    Integer insertRuntimeEntity(RuntimeEntity runtimeEntity);

    /**
     * 修改实例
     * @param runtimeEntity
     * @return
     */
    @Update("update runtime set " +
            "delete_flag = #{deleteFlag} " +
            "where id = #{id}")
    Integer updateRuntimeEntity(RuntimeEntity runtimeEntity);

    /**
     * 模糊查询多个实例
     * @param keyword
     * @return
     */
    @Select("select * from runtime " +
            "where delete_flag = 0 and " +
            "(id like #{keyword} or space_id like #{keyword} or case_source_type like #{keyword} or source_id like #{keyword} or node_id like #{keyword} " +
            "or server_ip like #{keyword} or server_port like #{keyword} start_time like #{keyword} or end_time like #{keyword} or estimate_start_time like #{keyword} or estimate_end_time like #{keyword} " +
            "or runtime_status like #{keyword} or label like #{keyword} or start_id like #{keyword} or start_name like #{keyword} or end_id like #{keyword} or end_name like #{keyword})")
    List<RuntimeEntity> queryRuntimeEntitysByKeyword(String keyword);

    /**
     * 查询多个实例
     * @param runtimeEntity
     * @return
     */
    @Select({"<script>" +
            "select * from runtime " +
            "<where>" +
            "<if test = 'operatorId != null'>operator_id = #{operatorId} </if>" +
            "<if test = 'startTime != null'>and start_time &gt; #{startTime} </if>" +
            "<if test = 'endTime != null'>and end_time &lt; #{endTime} </if>" +
            "<if test = 'estimateStartTime != null'>and estimate_start_time &gt; #{estimateStartTime} </if>" +
            "<if test = 'estimateEndTime != null'>and estimate_end_time &lt; #{estimateEndTime} </if>" +
            "</where>" +
            "</script>"})
    List<RuntimeEntity> queryRuntimeEntitys(RuntimeEntity runtimeEntity);

    /**
     * 查询单个实例
     * @param runtimeEntity
     * @return
     */
    @Select("select * from runtime " +
            "where id = #{id}")
    RuntimeEntity queryRuntimeEntity(RuntimeEntity runtimeEntity);
}