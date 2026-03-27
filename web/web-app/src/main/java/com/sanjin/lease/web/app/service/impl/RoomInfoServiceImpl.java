package com.sanjin.lease.web.app.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.sanjin.lease.common.app.BloomEnum;
import com.sanjin.lease.common.app.RoomEnum;
import com.sanjin.lease.common.utils.CacheClient;
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
import org.redisson.api.RedissonClient;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
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
    private RedissonClient redissonClient;

    @Autowired
    private CacheClient cacheClient;

    @Autowired
    private StringRedisTemplate stringRedisTemplate;


    /**
     * 根据房间 ID 获取房间详情信息
     *
     * <p>该方法采用多级缓存策略，通过布隆过滤器快速判断房间是否存在，
     * 然后依次从 Redis 缓存和数据库中查询数据，有效防止缓存穿透问题。</p>
     *
     * <p>执行流程：</p>
     * <ol>
     *     <li>通过布隆过滤器判断房间 ID 是否可能存在，不存在则直接返回</li>
     *     <li>尝试从 Redis 缓存中获取房间详情数据</li>
     *     <li>如果缓存未命中或查询异常，则从数据库中查询</li>
     *     <li>将数据库查询结果写入缓存（如果存在）或设置空值缓存（如果不存在）</li>
     * </ol>
     *
     * @param id 房间 ID，用于唯一标识一个房间，不能为空
     * @return RoomDetailVo 房间详情对象，包含房间的完整信息，具体包括：
     *         <ul>
     *             <li>公寓基本信息（apartmentItemVo）</li>
     *             <li>图片列表（graphVoList）</li>
     *             <li>属性值列表（attrValueVoList）</li>
     *             <li>配套设施列表（facilityInfoList）</li>
     *             <li>标签列表（labelInfoList）</li>
     *             <li>支付方式列表（paymentTypeList）</li>
     *             <li>费用值列表（feeValueVoList）</li>
     *             <li>租期列表（leaseTermList）</li>
     *         </ul>
     *         如果房间不存在或查询失败，返回 null
     */
    @Override
    public RoomDetailVo getRoomDetailById(Long id) {

        // 获取布隆过滤器实例，用于快速判断房间 ID 是否存在;
        // 1. 第一道防线：布隆过滤器
        log.info("开始检查布隆过滤器，房间 ID: {}", id);
        // 通过布隆过滤器判断房间是否一定不存在，如果不存在则直接返回，避免无效查询
        if (!redissonClient.getBloomFilter(BloomEnum.BLOOM_ROOM_KEY).contains(id)){
            log.warn("⚠️ 布隆过滤器拦截 - 房间一定不存在，房间 ID: {}", id);
            return null;
        }
        log.info("✓ 布隆过滤器通过 - 房间可能存在，房间 ID: {}", id);
        String key = RoomEnum.ROOM_INFO_KEY + id;
        log.info("开始查询房间详情，房间 ID: {}", id);

        // 2. 第二道防线：查询 Redis (显式区分 Key 是否存在)
        // 注意：这里我们先拿原始字符串，不直接用工具类的 get
        // 尝试从 Redis 缓存中获取房间详情数据，优先使用缓存以提高查询性能
//        String json = stringRedisTemplate.opsForValue().get(key);
        try{
            if (cacheClient.hasKey( key)){
                log.info("命中空值缓存，直接返回，不再查库。ID: {}", id);
                return cacheClient.get(key, RoomDetailVo.class);
            }
            RoomDetailVo roomDetailVo = cacheClient.get(RoomEnum.ROOM_INFO_KEY + id, RoomDetailVo.class);
            if (roomDetailVo != null) {
                log.info("缓存命中，房间 ID: {}", id);
                return roomDetailVo;
            }
            log.info("缓存未命中，房间 ID: {}", id);
        }catch (Exception e){
            log.error("查询缓存异常，房间 ID: {}, 错误信息：{}", id, e.getMessage(), e);
        }
        // 3. 缓存未命中，开始查库缓存未命中或查
        // 询异常时，从数据库中查询房间详情数据
        log.info("开始从数据库查询房间详情，房间 ID: {}", id);
        RoomDetailVo fromDb = getFromDb(id);
        if (fromDb != null){
            // 将查询到的房间详情写入 Redis 缓存，设置 30 分钟过期时间，提高后续查询效率
            cacheClient.setWithRandomTTL(RoomEnum.ROOM_INFO_KEY + id, fromDb,
                    300L,60L, TimeUnit.SECONDS);
            log.info("房间详情已缓存，房间 ID: {}", id);
            return fromDb;
        }else{
            // 数据库中也不存在该房间，设置空值缓存防止缓存穿透，过期时间为 2 分钟
            cacheClient.set(RoomEnum.ROOM_INFO_KEY + id, null, 30L, TimeUnit.SECONDS);
            log.warn("房间不存在，设置空值缓存，房间 ID: {}", id);
        }

        log.info("房间详情查询结束，未找到数据，房间 ID: {}", id);
        return null;
    }


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

    private RoomDetailVo getFromDb(Long id) {
        RoomInfo roomInfo = roomInfoMapper.selectById(id);
        if (roomInfo == null || roomInfo.getIsDeleted() == (byte)1) {
            // 数据库也没有，设置空值缓存并返回
            cacheClient.set(RoomEnum.ROOM_INFO_KEY + id, null, 30L, TimeUnit.SECONDS);
            log.warn("房间不存在或已删除，设置空值缓存，ID: {}", id);
            return null;
        }
        //查询缓存中的房间详情数据
        RoomDetailVo roomDetailVo = cacheClient.get(RoomEnum.ROOM_INFO_KEY + id, RoomDetailVo.class);
        if (roomDetailVo != null){
            return roomDetailVo;
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




