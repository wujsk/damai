package com.damai.controller;


import com.damai.common.ApiResponse;
import com.damai.dto.ChannelDataAddDto;
import com.damai.dto.GetChannelDataByCodeDto;
import com.damai.service.ChannelDataService;
import com.damai.vo.GetChannelDataVo;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author: haonan
 * @description: 渠道 控制层
 */
@RestController
@RequestMapping("/channel/data")
@Tag(name = "channel-data", description = "渠道数据")
public class ChannelDataController {

    @Resource
    private ChannelDataService channelDataService;

    @Operation(summary = "通过code查询渠道数据")
    @PostMapping(value = "/getByCode")
    public ApiResponse<GetChannelDataVo> getByCode(@Valid @RequestBody GetChannelDataByCodeDto getChannelDataByCodeDto) {
        GetChannelDataVo getChannelDataVo = channelDataService.getByCode(getChannelDataByCodeDto);
        return ApiResponse.ok(getChannelDataVo);
    }

    @Operation(summary = "添加渠道数据")
    @PostMapping(value = "/add")
    public ApiResponse<Boolean> add(@Valid @RequestBody ChannelDataAddDto channelDataAddDto) {
        channelDataService.add(channelDataAddDto);
        return ApiResponse.ok(true);
    }
}
