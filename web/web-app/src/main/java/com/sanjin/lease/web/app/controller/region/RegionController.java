package com.sanjin.lease.web.app.controller.region;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.sanjin.lease.common.result.Result;
import com.sanjin.lease.model.entity.CityInfo;
import com.sanjin.lease.model.entity.DistrictInfo;
import com.sanjin.lease.model.entity.ProvinceInfo;
import com.sanjin.lease.web.app.service.CityInfoService;
import com.sanjin.lease.web.app.service.DistrictInfoService;
import com.sanjin.lease.web.app.service.ProvinceInfoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Tag(name = "地区信息")
@RestController
@RequestMapping("/app/region")
public class RegionController {

    @Autowired
    private ProvinceInfoService provinceInfoService;

    @Autowired
    private CityInfoService cityInfoService;

    @Autowired
    private DistrictInfoService districtInfoService;

    @Operation(summary="查询省份信息列表")
    @GetMapping("province/list")
    public Result<List<ProvinceInfo>> listProvince(){
        return Result.ok(provinceInfoService.list());
    }

    @Operation(summary="根据省份id查询城市信息列表")
    @GetMapping("city/listByProvinceId")
    public Result<List<CityInfo>> listCityInfoByProvinceId(@RequestParam Long id){
        LambdaQueryWrapper<CityInfo> cityInfoLambdaQueryWrapper =
                new LambdaQueryWrapper<>();
        cityInfoLambdaQueryWrapper.eq(CityInfo::getProvinceId,id);
        return Result.ok(cityInfoService.list(cityInfoLambdaQueryWrapper));
    }

    @GetMapping("district/listByCityId")
    @Operation(summary="根据城市id查询区县信息")
    public Result<List<DistrictInfo>> listDistrictInfoByCityId(@RequestParam Long id){
        LambdaQueryWrapper<DistrictInfo> districtInfoLambdaQueryWrapper =
                new LambdaQueryWrapper<>();
        districtInfoLambdaQueryWrapper.eq(DistrictInfo::getCityId,id);
        return Result.ok(districtInfoService.list(districtInfoLambdaQueryWrapper));
    }
}
