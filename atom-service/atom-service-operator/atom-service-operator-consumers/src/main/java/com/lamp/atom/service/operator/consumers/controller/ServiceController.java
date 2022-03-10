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

import org.apache.dubbo.config.annotation.Reference;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.lamp.atom.schedule.api.Shedule;
import com.lamp.atom.schedule.core.AtomScheduleService;
import com.lamp.atom.service.operator.consumers.function.PortCreatingFunction;
import com.lamp.atom.service.operator.entity.ServiceInfo;
import com.lamp.atom.service.operator.service.ServiceInfoService;

import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;

/**
 * 推理
 * 
 * @author laohu
 *
 */
@Slf4j
@RequestMapping("/service")
@RestController("serviceController")
@Api(hidden = true)
public class ServiceController {

    @Autowired
    private PortCreatingFunction portCreatingFunction;
    
    @Autowired
    private AtomScheduleService atomScheduleService;
    
    @Reference
    private ServiceInfoService serviceInfoService;
    

    @PostMapping("/getServicePort")
    public Long getServicePort(@RequestBody String ip) {
        return portCreatingFunction.getPort(ip).longValue();
    }
    
    @RequestMapping("/createRuntime")
    public Integer createRuntime(ServiceInfo serviceInfo) {
    	// 查询服务状态
    	
    	// 如果服务在运行中，那么退出
    	
    	// 如果是停止，那么从新创建一个
    	
    	// 如果是等待运行，
    	Shedule shedule = new Shedule();
    	atomScheduleService.createService(shedule);
    	// 修改name 与 状态
    	return 0;
    }
    
    @RequestMapping("/dropRuntime")
    public Integer dropRuntime(ServiceInfo serviceInfo) {
    	Shedule shedule = new Shedule();
    	//
    	atomScheduleService.closeService(shedule);
    	return 0;
    }

}
