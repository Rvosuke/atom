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
package com.lamp.atom.service.operator.consumers.controller;

import com.lamp.atom.service.domain.NodeStatus;
import com.lamp.atom.service.domain.RelationType;
import com.lamp.atom.service.domain.ResourceType;
import com.lamp.atom.service.operator.consumers.utils.ResultObjectEnums;
import com.lamp.atom.service.operator.domain.NodeInformation;
import com.lamp.atom.service.operator.domain.NodeRelation;
import com.lamp.atom.service.operator.entity.*;
import com.lamp.atom.service.operator.service.*;
import com.lamp.decoration.core.result.ResultObject;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.Reference;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import springfox.documentation.annotations.ApiIgnore;

import java.util.HashMap;
import java.util.List;
import java.util.Objects;

@Slf4j
@RequestMapping("/node")
@RestController("nodeController")
@Api(tags = {"节点操作接口"})
public class NodeController {

    @Reference
    private NodeService nodeService;
    @Reference
    private ResourceRelationService resourceRelationService;
    @Reference
    private OperatorService operatorService;
    @Reference
    private ModelService modelService;
    @Reference
    private DataSourceService dataSourceService;
    @Reference
    private ServiceInfoService serviceInfoService;
    @Reference
    private RuntimeService runtimeService;

    /**
     * 查询节点所有信息
     *
     * @param nodeEntity
     */
    @PostMapping("/queryNodeInformation")
    @ApiOperation(value = "查询节点所有信息")
    public NodeInformation queryNodeInformation(@RequestBody NodeEntity nodeEntity) {
        NodeInformation nodeInformation = new NodeInformation();

        // 1、查询节点实体
        nodeEntity = nodeService.queryNodeEntity(nodeEntity);
        nodeInformation.setNodeEntity(nodeEntity);

        // 2、查询节点所属实验
        ResourceRelationEntity examRelationEntity = new ResourceRelationEntity();
        examRelationEntity.setRelationType(RelationType.NODE_RELATION);
        examRelationEntity.setRelateId(nodeEntity.getId());
        examRelationEntity.setRelateType(ResourceType.ORGANIZATION);
        List<ResourceRelationEntity> examRelationList = resourceRelationService.queryResourceRelationEntitys(examRelationEntity);
        nodeInformation.setExamRelationList(examRelationList);

        // 3、查询节点的依赖资源
        ResourceRelationEntity resourceRelationEntity = new ResourceRelationEntity();
        resourceRelationEntity.setRelationType(RelationType.RESOURCE_RELATION);
        resourceRelationEntity.setRelateId(nodeEntity.getId());
        resourceRelationEntity.setRelateType(ResourceType.NODE);
        List<ResourceRelationEntity> resourceRelationList = resourceRelationService.queryResourceRelationEntitys(resourceRelationEntity);
        for (ResourceRelationEntity relationEntity : resourceRelationList) {
            // 算子
            if (relationEntity.getBeRelatedType() == ResourceType.OPERATOR) {
                OperatorEntity operator = new OperatorEntity();
                operator.setId(relationEntity.getBeRelatedId());
                nodeInformation.setOperatorEntity(operatorService.queryOperatorEntity(operator));
            }
            // 模型
            if (relationEntity.getBeRelatedType() == ResourceType.MODEL) {
                ModelEntity model = new ModelEntity();
                model.setId(relationEntity.getBeRelatedId());
                nodeInformation.setModelEntity(modelService.queryModelEntity(model));
            }
            // 数据源
            if (relationEntity.getBeRelatedType() == ResourceType.DATASOURCE) {
                DataSourceEntity dataSource = new DataSourceEntity();
                dataSource.setId(relationEntity.getBeRelatedId());
                nodeInformation.setDataSourceEntity(dataSourceService.queryDataSourceEntity(dataSource));
            }
            // 配置
            if (relationEntity.getBeRelatedType() == ResourceType.SERVICE_INFO) {
                ServiceInfoEntity serviceInfo = new ServiceInfoEntity();
                serviceInfo.setId(relationEntity.getBeRelatedId());
                nodeInformation.setServiceInfoEntity(serviceInfoService.queryServiceInfoEntity(serviceInfo));
            }
        }

        // 3、查询节点的Runtime信息
        RuntimeEntity runtimeEntity = new RuntimeEntity();
        runtimeEntity.setNodeId(nodeEntity.getId());
        List<RuntimeEntity> runtimeEntityList = runtimeService.queryRuntimeEntitys(runtimeEntity);
        nodeInformation.setRuntimeEntityList(runtimeEntityList);

        return nodeInformation;
    }

    /**
     * 创建节点以及依赖关系
     *
     * @param nodeRelation
     */
    @PostMapping("/createNodeRelation")
    @ApiOperation(value = "创建节点以及依赖关系")
    public ResultObject<String> createNodeRelation(@RequestBody NodeRelation nodeRelation) {
        // 1、字段判空
        if (Objects.isNull(nodeRelation.getNodeId())
                || Objects.isNull(nodeRelation.getModelID())
                || Objects.isNull(nodeRelation.getDataSourceId())
                || Objects.isNull(nodeRelation.getServiceInfoId())
                || Objects.isNull(nodeRelation.getMaxServiceInfoId())
                || Objects.isNull(nodeRelation.getMinServiceInfoId())) {
            log.info("参数校验失败 {}", nodeRelation);
            return ResultObjectEnums.CHECK_PARAMETERS_FAIL.getResultObject();
        }

        // 2、修改节点
        NodeEntity node = new NodeEntity();
        node.setId(nodeRelation.getNodeId());
        node.setNodeStatus(NodeStatus.EDIT_FINISH);
        nodeService.updateNodeEntity(node);

        // 3、创建依赖关系（模型（包含算子）、数据源（包含连接）、服务信息、服务最大配置、服务最小配置）
        ResourceRelationEntity operatorRelationEntity = new ResourceRelationEntity();
        operatorRelationEntity.setRelationType(RelationType.RESOURCE_RELATION);
        operatorRelationEntity.setRelateId(node.getId());
        operatorRelationEntity.setRelateType(ResourceType.NODE);
        operatorRelationEntity.setBeRelatedId(nodeRelation.getModelID());
        operatorRelationEntity.setBeRelatedType(ResourceType.MODEL);
        operatorRelationEntity.setRelationStatus("relate");
        operatorRelationEntity.setOrder(1);

        ResourceRelationEntity dataSourceRelationEntity = new ResourceRelationEntity();
        dataSourceRelationEntity.setRelationType(RelationType.RESOURCE_RELATION);
        dataSourceRelationEntity.setRelateId(node.getId());
        dataSourceRelationEntity.setRelateType(ResourceType.NODE);
        dataSourceRelationEntity.setBeRelatedId(nodeRelation.getDataSourceId());
        dataSourceRelationEntity.setBeRelatedType(ResourceType.DATASOURCE);
        dataSourceRelationEntity.setRelationStatus("relate");
        dataSourceRelationEntity.setOrder(1);

        ResourceRelationEntity serviceInfoEntity = new ResourceRelationEntity();
        serviceInfoEntity.setRelationType(RelationType.RESOURCE_RELATION);
        serviceInfoEntity.setRelateId(node.getId());
        serviceInfoEntity.setRelateType(ResourceType.NODE);
        serviceInfoEntity.setBeRelatedId(nodeRelation.getServiceInfoId());
        serviceInfoEntity.setBeRelatedType(ResourceType.SERVICE_INFO);
        serviceInfoEntity.setRelationStatus("relate");
        serviceInfoEntity.setOrder(1);

        ResourceRelationEntity maxServiceInfoEntity = new ResourceRelationEntity();
        maxServiceInfoEntity.setRelationType(RelationType.RESOURCE_RELATION);
        maxServiceInfoEntity.setRelateId(node.getId());
        maxServiceInfoEntity.setRelateType(ResourceType.NODE);
        maxServiceInfoEntity.setBeRelatedId(nodeRelation.getServiceInfoId());
        maxServiceInfoEntity.setBeRelatedType(ResourceType.MAX_SERVICE_INFO);
        maxServiceInfoEntity.setRelationStatus("relate");
        maxServiceInfoEntity.setOrder(1);

        ResourceRelationEntity minServiceInfoEntity = new ResourceRelationEntity();
        minServiceInfoEntity.setRelationType(RelationType.RESOURCE_RELATION);
        minServiceInfoEntity.setRelateId(node.getId());
        minServiceInfoEntity.setRelateType(ResourceType.NODE);
        minServiceInfoEntity.setBeRelatedId(nodeRelation.getServiceInfoId());
        minServiceInfoEntity.setBeRelatedType(ResourceType.MIN_SERVICE_INFO);
        minServiceInfoEntity.setRelationStatus("relate");
        minServiceInfoEntity.setOrder(1);

        resourceRelationService.insertResourceRelationEntity(operatorRelationEntity);
        resourceRelationService.insertResourceRelationEntity(dataSourceRelationEntity);
        resourceRelationService.insertResourceRelationEntity(serviceInfoEntity);
        resourceRelationService.insertResourceRelationEntity(maxServiceInfoEntity);
        resourceRelationService.insertResourceRelationEntity(minServiceInfoEntity);

        return ResultObjectEnums.SUCCESS.getResultObject();
    }

    /**
     * 添加节点
     *
     * @param nodeEntity
     */
    @PostMapping("/insertNode")
    @ApiOperation(value = "添加节点")
    public ResultObject<String> insertNode(@RequestBody NodeEntity nodeEntity) {
        // 字段判空
        if (Objects.isNull(nodeEntity.getSpaceId()) || Objects.isNull(nodeEntity.getNodeTemplateId())) {
            log.info("参数校验失败 {}", nodeEntity);
            return ResultObjectEnums.CHECK_PARAMETERS_FAIL.getResultObject();
        }
        try {
            nodeService.insertNodeEntity(nodeEntity);
        } catch (Exception e) {
            log.warn("节点插入失败 {}", e);
            return ResultObjectEnums.FAIL.getResultObject();
        }
        return ResultObjectEnums.SUCCESS.getResultObject();
    }

    /**
     * 修改节点
     *
     * @param nodeEntity
     * @return
     */
    @PostMapping("/updateNode")
    @ApiOperation(value = "修改节点")
    @ApiImplicitParams({
            @ApiImplicitParam(name="id",dataTypeClass = java.lang.Long.class,paramType="body" ,dataType = "Long"),
            @ApiImplicitParam(name="nodeStatus",dataTypeClass = java.lang.Long.class,paramType="body" ,dataType = "String"),
            @ApiImplicitParam(name="deleteFlag",dataTypeClass = java.lang.Long.class,paramType="body" ,dataType = "Long")
    })
    public ResultObject<String> updateNode(@ApiIgnore @RequestBody NodeEntity nodeEntity) {
        try {
            nodeService.updateNodeEntity(nodeEntity);
        } catch (Exception e) {
            log.warn("节点修改失败 {}", e);
            return ResultObjectEnums.FAIL.getResultObject();
        }
        return ResultObjectEnums.SUCCESS.getResultObject();
    }

    /**
     * 模糊查询多个节点
     *
     * @param params
     * @return
     */
    @PostMapping("/queryNodesByKeyword")
    @ApiOperation(value = "模糊查询多个节点")
    public List<NodeEntity> queryNodesByKeyword(@RequestBody HashMap<String, String> params) {
        try {
            return nodeService.queryNodeEntitysByKeyword(params.get("keyword"));
        } catch (Exception e) {
            log.warn("节点查询失败 {}", e);
            return null;
        }
    }

    /**
     * 查询多个节点
     *
     * @param nodeEntity
     * @return
     */
    @PostMapping("/queryNodes")
    @ApiOperation(value = "查询多个节点")
    public List<NodeEntity> queryNodes(@RequestBody(required = false) NodeEntity nodeEntity) {
        try {
            return nodeService.queryNodeEntitys(nodeEntity);
        } catch (Exception e) {
            log.warn("节点查询失败 {}", e);
            return null;
        }
    }

    /**
     * 查询单个节点
     *
     * @param nodeEntity
     * @return
     */
    @PostMapping("/queryNode")
    @ApiOperation(value = "查询单个节点")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", paramType = "body" ,dataType = "Long", dataTypeClass = java.lang.Long.class, defaultValue = "1")
    })
    public NodeEntity queryNode(@ApiIgnore @RequestBody NodeEntity nodeEntity) {
        try {
            NodeEntity nodeEntity1 = nodeService.queryNodeEntity(nodeEntity);
            return nodeEntity1;
        } catch (Exception e) {
            log.warn("节点查询失败 {}", e);
            return null;
        }
    }
}
