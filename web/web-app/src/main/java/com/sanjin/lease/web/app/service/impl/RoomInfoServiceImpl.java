package com.sanjin.lease.web.app.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.sanjin.lease.common.app.RoomEnum;
import com.sanjin.lease.model.entity.*;
import com.sanjin.lease.model.enums.ItemType;
import com.sanjin.lease.web.app.mapper.*;
import com.sanjin.lease.web.app.service.ApartmentInfoService;
import com.sanjin.lease.web.app.service.RoomInfoService;
import com.sanjin.lease.web.app.utils.CacheClient;
import com.sanjin.lease.web.app.vo.apartment.ApartmentItemVo;
import com.sanjin.lease.web.app.vo.attr.AttrValueVo;
import com.sanjin.lease.web.app.vo.fee.FeeValueVo;
import com.sanjin.lease.web.app.vo.graph.GraphVo;
import com.sanjin.lease.web.app.vo.room.RoomDetailVo;
import com.sanjin.lease.web.app.vo.room.RoomItemVo;
import com.sanjin.lease.web.app.vo.room.RoomQueryVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
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
    private CacheClient cacheClient;
    @Autowired
    private com.sanjin.lease.web.admin.mapper.ApartmentInfoMapper apartmentInfoMapper;

        /**
     * 根据房间 ID 查询房间详细信息
     * 采用 Cache Aside 模式：先查缓存，未命中则查数据库并回写缓存
     *
     * @param id 房间 ID，用于查询和缓存键生成
     * @return RoomDetailVo 房间详细信息对象，包含：
     *         - 房间基本信息（租金、房间号等）
     *         - 所在公寓信息
     *         - 图片列表
     *         - 属性值列表
     *         - 配套设施列表
     *         - 标签列表
     *         - 支付方式列表
     *         - 费用值列表
     *         - 租期列表
     *         如果房间不存在，返回 null
     */
    @Override
    public RoomDetailVo getRoomDetailById(Long id) {
        // 查询缓存中的房间详情数据
        RoomDetailVo roomDetailVo = cacheClient.get(RoomEnum.ROOM_INFO_KEY + id, RoomDetailVo.class);
        if (roomDetailVo != null){
            return roomDetailVo;
        }

        RoomInfo roomInfo = roomInfoMapper.selectById(id);
        if (roomInfo == null) {
            return null; // 数据库也没有，直接返回
        }

        // 从数据库查询房间关联的公寓信息
        ApartmentItemVo apartmentItemVo =
                apartmentInfoService.getApartmentItemVoById(roomInfo.getApartmentId());

        // 从数据库查询房间的图片列表
        List<GraphVo> graphVoList =
                 graphInfoMapper.selectListByItemTypeAndId(ItemType.ROOM,id);

        // 从数据库查询房间的属性值列表
        List<AttrValueVo> attrValueVoList =
                attrValueMapper.selectListAttrValueVoByRoomId(id);

        // 从数据库查询房间的配套设施列表
        List<FacilityInfo> facilityInfoList =
                facilityInfoMapper.selectListFacilityInfoRoomById(id);

        List<LabelInfo> labelInfoList =
                labelInfoMapper.selectListLabelInfoRoomById(id);

        // 从数据库查询房间支持的支付方式列表
        List<PaymentType> paymentTypeList =
                paymentTypeMapper.selectListPaymentTypeRoomById(id);

        // 从数据库查询房间的费用值列表
        List<FeeValueVo> feeValueVoList =
                feeValueMapper.selectListFeeValueRoomById(id);

        // 从数据库查询房间的租期列表
        List<LeaseTerm> leaseTermList =
                leaseTermMapper.selectListLeaseTermRoomById(id);

        // 组装完整的房间详情对象
        roomDetailVo = new RoomDetailVo();
        BeanUtils.copyProperties(roomInfo,roomDetailVo);
        roomDetailVo.setApartmentItemVo(apartmentItemVo);
        roomDetailVo.setGraphVoList(graphVoList);
        roomDetailVo.setAttrValueVoList(attrValueVoList);
        roomDetailVo.setFacilityInfoList(facilityInfoList);
        roomDetailVo.setLabelInfoList(labelInfoList);
        roomDetailVo.setPaymentTypeList(paymentTypeList);
        roomDetailVo.setFeeValueVoList(feeValueVoList);
        roomDetailVo.setLeaseTermList(leaseTermList);

        // 将组装好的房间详情数据写入 Redis 缓存，设置 30 分钟过期时间
        cacheClient.set(RoomEnum.ROOM_INFO_KEY + id, roomDetailVo,
                30L, TimeUnit.MINUTES);
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




