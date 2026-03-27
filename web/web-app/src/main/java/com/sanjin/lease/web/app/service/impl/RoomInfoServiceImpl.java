package com.sanjin.lease.web.app.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.sanjin.lease.common.exception.LeaseException;
import com.sanjin.lease.model.entity.*;
import com.sanjin.lease.model.enums.ItemType;
import com.sanjin.lease.web.app.mapper.*;
import com.sanjin.lease.web.app.service.ApartmentInfoService;
import com.sanjin.lease.web.app.service.RoomInfoService;
import com.sanjin.lease.web.app.vo.apartment.ApartmentItemVo;
import com.sanjin.lease.web.app.vo.attr.AttrValueVo;
import com.sanjin.lease.web.app.vo.fee.FeeValueVo;
import com.sanjin.lease.web.app.vo.graph.GraphVo;
import com.sanjin.lease.web.app.vo.room.RoomDetailVo;
import com.sanjin.lease.web.app.vo.room.RoomItemVo;
import com.sanjin.lease.web.app.vo.room.RoomQueryVo;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RBloomFilter;
import org.redisson.api.RBucket;
import org.redisson.api.RedissonClient;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;

/**
 * @author liubo
 * @description 针对表【room_info(房间信息表)】的数据库操作Service实现
 * @createDate 2023-07-26 11:12:39
 */
@Service
@Slf4j
public class RoomInfoServiceImpl extends ServiceImpl<RoomInfoMapper, RoomInfo>
        implements RoomInfoService {


    @Autowired
    private RoomInfoMapper roomInfoMapper;
    @Autowired
    private ApartmentInfoService apartmentInfoService;
    @Autowired
    private GraphInfoMapper graphInfoMapper;
    @Autowired
    private AttrValueMapper attrValueMapper;

    @Autowired
    private FacilityInfoMapper facilityInfoMapper;

    @Autowired
    private LabelInfoMapper labelInfoMapper;

    @Autowired
    private PaymentTypeMapper paymentTypeMapper;

    @Autowired
    private LeaseTermMapper leaseTermMapper;

    @Autowired
    private FeeValueMapper feeValueMapper;

    @Autowired
    private RedissonClient redissonClient;
    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    // 根据查询房间详情
    @Override
    public RoomDetailVo getRoomDetailById(Long id) {
        RBloomFilter<Long> bloomFilter = redissonClient.getBloomFilter("bloom:room:id");
        if (!bloomFilter.contains(id)){
            // 布隆说没有，那就是真没有，直接拦截恶意穿透！
            throw new LeaseException("非法请求，该房源不存在!",210);
        }

        //如果缓存命中，则直接返回
        String redisKey = "room:detail:" + id;
        RBucket<RoomDetailVo> bucket = redissonClient.getBucket(redisKey);
        RoomDetailVo cachedVo = bucket.get();
        if (cachedVo != null){
            return cachedVo;
        }


        //查询房间信息
        RoomInfo roomInfo = roomInfoMapper.selectRoomById(id);
        if (roomInfo == null){
            throw new LeaseException("该房源不存在", 210);
        }
        //查询所在公寓信息
        ApartmentItemVo apartmentItemVo =
                apartmentInfoService.getApartmentItemVoById(roomInfo.getApartmentId());

        //查询图片列表
        List<GraphVo> graphVoList =
                 graphInfoMapper.selectListByItemTypeAndId(ItemType.ROOM,id);

        //查询房间属性信息列表
        List<AttrValueVo> attrValueVoList =
                attrValueMapper.selectListAttrValueVoByRoomId(id);

        //查询房间配套信息列表
        List<FacilityInfo> facilityInfoList =
                facilityInfoMapper.selectListFacilityInfoRoomById(id);

        List<LabelInfo> labelInfoList =
                labelInfoMapper.selectListLabelInfoRoomById(id);

        //查询房间支付方式列表
        List<PaymentType> paymentTypeList =
                paymentTypeMapper.selectListPaymentTypeRoomById(id);

        List<FeeValueVo> feeValueVoList =
                feeValueMapper.selectListFeeValueRoomById(id);

        List<LeaseTerm> leaseTermList =
                leaseTermMapper.selectListLeaseTermRoomById(id);

        RoomDetailVo roomDetailVo = new RoomDetailVo();
        BeanUtils.copyProperties(roomInfo,roomDetailVo);
        roomDetailVo.setApartmentItemVo(apartmentItemVo);
        roomDetailVo.setGraphVoList(graphVoList);
        roomDetailVo.setAttrValueVoList(attrValueVoList);
        roomDetailVo.setFacilityInfoList(facilityInfoList);
        roomDetailVo.setLabelInfoList(labelInfoList);
        roomDetailVo.setPaymentTypeList(paymentTypeList);
        roomDetailVo.setFeeValueVoList(feeValueVoList);
        roomDetailVo.setLeaseTermList(leaseTermList);
        long expireTime = 30 * 60 + new Random().nextInt(5 * 60);
        bucket.set(roomDetailVo, expireTime, TimeUnit.SECONDS);
        return roomDetailVo;
    }

    // 根据公寓id分页查询房间列表
    @Override
    public IPage<RoomItemVo> pageItemByApartmentId(Page<RoomItemVo> page, Long id) {
        return roomInfoMapper.pageItemByApartmentId(page, id);
    }


    // 根据查询条件分页查询房间列表
    @Override
    public IPage<RoomItemVo> pageRoomItemByQuery(Page<RoomItemVo> page, RoomQueryVo queryVo) {
        return roomInfoMapper.pageRoomItemByQuery(page, queryVo);
    }
}




