package com.bixin.ido.service.impl;

import com.bixin.ido.bean.DO.IdoDxAttribute;
import com.bixin.ido.bean.DO.IdoDxLabel;
import com.bixin.ido.bean.DO.IdoDxLink;
import com.bixin.ido.bean.DO.IdoDxProduct;
import com.bixin.ido.bean.vo.HomeProductVO;
import com.bixin.ido.core.mapper.IdoDxAttributeMapper;
import com.bixin.ido.core.mapper.IdoDxLabelMapper;
import com.bixin.ido.core.mapper.IdoDxLinkMapper;
import com.bixin.ido.core.mapper.IdoDxProductMapper;
import com.bixin.ido.core.wrapDDL.IdoDxAttributeDDL;
import com.bixin.ido.core.wrapDDL.IdoDxLabelDDL;
import com.bixin.ido.core.wrapDDL.IdoDxLinkDDL;
import com.bixin.ido.core.wrapDDL.IdoDxProductDDL;
import com.bixin.ido.common.enums.ProductState;
import com.bixin.ido.service.IDxProductService;
import com.bixin.common.utils.LocalDateTimeUtil;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author zhangcheng
 * create 2021-08-06 5:00 下午
 */
@Service
public class DxProductImpl implements IDxProductService {

    @Resource
    IdoDxProductMapper idoDxProductMapper;

    @Resource
    IdoDxAttributeMapper idoDxAttributeMapper;

    @Resource
    IdoDxLinkMapper idoDxLinkMapper;

    @Resource
    IdoDxLabelMapper idoDxLabelMapper;

    @Override
    public List<HomeProductVO> getHomeProducts(ProductState productState) {

        IdoDxProductDDL dxProductDDL = new IdoDxProductDDL();
        IdoDxProductDDL.Criteria criteria = dxProductDDL.createCriteria();

        Long currentTime = LocalDateTimeUtil.getMilliByTime(LocalDateTime.now());

        if (ProductState.PREVIEW == productState) {
            criteria.andStartTimeEqualTo(0L);
        } else if (ProductState.INIT == productState) {
            criteria.andStartTimeGreaterThan(0L)
                    .andStartTimeGreaterThan(currentTime);
        } else if (ProductState.PROCESSING == productState) {
            criteria.andStartTimeGreaterThan(0L)
                    .andStartTimeLessThanOrEqualTo(currentTime)
                    .andEndTimeGreaterThan(currentTime);
        } else if (ProductState.FINISH == productState) {
            criteria.andStartTimeGreaterThan(0L)
                    .andEndTimeGreaterThan(0L)
                    .andEndTimeLessThanOrEqualTo(currentTime);
        }
        dxProductDDL.setOrderByClause("createTime desc");

        List<IdoDxProduct> idoDxProducts = idoDxProductMapper.selectByDDL(dxProductDDL);

        if (CollectionUtils.isEmpty(idoDxProducts)) {
            return Collections.emptyList();
        }

        List<Long> pIds = idoDxProducts.stream().map(IdoDxProduct::getId).collect(Collectors.toList());

        //属性
        IdoDxAttributeDDL dxAttributeDDL = new IdoDxAttributeDDL();
        IdoDxAttributeDDL.Criteria attributeCriteria = dxAttributeDDL.createCriteria();
        attributeCriteria.andPrdIdIn(pIds);
        List<IdoDxAttribute> idoDxAttributes = idoDxAttributeMapper.selectByDDL(dxAttributeDDL);

        //快捷链接
        IdoDxLinkDDL dxLinkDDL = new IdoDxLinkDDL();
        IdoDxLinkDDL.Criteria linkCriteria = dxLinkDDL.createCriteria();
        linkCriteria.andPrdIdIn(pIds);
        List<IdoDxLink> idoDxLinks = idoDxLinkMapper.selectByDDL(dxLinkDDL);

        //标签
        IdoDxLabelDDL dxLabelDDL = new IdoDxLabelDDL();
        IdoDxLabelDDL.Criteria labelCriteria = dxLabelDDL.createCriteria();
        labelCriteria.andPrdIdIn(pIds);
        List<IdoDxLabel> idoDxLabels = idoDxLabelMapper.selectByDDL(dxLabelDDL);

        //分组
        Map<Long, List<IdoDxAttribute>> dxAttrMap = Collections.emptyMap();
        Map<Long, List<IdoDxLink>> dxLinkMap = Collections.emptyMap();
        Map<Long, List<IdoDxLabel>> dxLabelMap = Collections.emptyMap();
        if (!CollectionUtils.isEmpty(idoDxAttributes)) {
            dxAttrMap = idoDxAttributes.stream().collect(Collectors.groupingBy(IdoDxAttribute::getPrdId));
        }
        if (!CollectionUtils.isEmpty(idoDxLinks)) {
            dxLinkMap = idoDxLinks.stream().collect(Collectors.groupingBy(IdoDxLink::getPrdId));
        }
        if (!CollectionUtils.isEmpty(idoDxLabels)) {
            dxLabelMap = idoDxLabels.stream().collect(Collectors.groupingBy(IdoDxLabel::getPrdId));
        }

        //组合
        ArrayList<HomeProductVO> homeProductVOS = new ArrayList<>();
        for (IdoDxProduct p : idoDxProducts) {
            Long pId = p.getId();
            List<IdoDxAttribute> dxAttributeList = dxAttrMap.get(pId);
            List<IdoDxLink> idoDxLinkList = dxLinkMap.get(pId);
            List<IdoDxLabel> idoDxLabelList = dxLabelMap.get(pId);

            HomeProductVO homeProductVO = HomeProductVO.builder()
                    .attributes(dxAttributeList)
                    .labels(idoDxLabelList)
                    .links(idoDxLinkList)
                    .build();
            BeanUtils.copyProperties(p, homeProductVO);
            homeProductVOS.add(homeProductVO);
        }
        return homeProductVOS;
    }

    @Override
    public List<IdoDxProduct> getProducts4UpdateState(long currentTime, ProductState productState) {

        IdoDxProductDDL dxProductDDL = new IdoDxProductDDL();
        IdoDxProductDDL.Criteria criteria = dxProductDDL.createCriteria();

        if (ProductState.PROCESSING == productState) {
            criteria.andStartTimeLessThanOrEqualTo(currentTime)
                    .andEndTimeGreaterThan(currentTime);
            criteria.andStateEqualTo(ProductState.INIT.getDesc());
        } else if (ProductState.FINISH == productState) {
            criteria.andEndTimeLessThanOrEqualTo(currentTime);
            criteria.andStateEqualTo(ProductState.PROCESSING.getDesc());
        }

        return idoDxProductMapper.selectByDDL(dxProductDDL);
    }

    @Override
    public List<IdoDxProduct> getLastFinishProducts(long intervalTime) {
        Long currentTime = LocalDateTimeUtil.getMilliByTime(LocalDateTime.now());

        IdoDxProductDDL dxProductDDL = new IdoDxProductDDL();
        IdoDxProductDDL.Criteria criteria = dxProductDDL.createCriteria();
        criteria.andStateIn(List.of(ProductState.PROCESSING.getDesc(), ProductState.FINISH.getDesc()));
        criteria.andEndTimeGreaterThan(currentTime - intervalTime);

        return idoDxProductMapper.selectByDDL(dxProductDDL);
    }


    @Override
    public HomeProductVO getProduct(long pId) {
        IdoDxProduct idoDxProduct = idoDxProductMapper.selectByPrimaryKey(pId);
        if (Objects.isNull(idoDxProduct)) {
            return null;
        }

        //属性
        IdoDxAttributeDDL dxAttributeDDL = new IdoDxAttributeDDL();
        IdoDxAttributeDDL.Criteria attributeCriteria = dxAttributeDDL.createCriteria();
        attributeCriteria.andPrdIdEqualTo(pId);
        List<IdoDxAttribute> idoDxAttributes = idoDxAttributeMapper.selectByDDL(dxAttributeDDL);

        //快捷链接
        IdoDxLinkDDL dxLinkDDL = new IdoDxLinkDDL();
        IdoDxLinkDDL.Criteria linkCriteria = dxLinkDDL.createCriteria();
        linkCriteria.andPrdIdEqualTo(pId);
        List<IdoDxLink> idoDxLinks = idoDxLinkMapper.selectByDDL(dxLinkDDL);

        //标签
        IdoDxLabelDDL dxLabelDDL = new IdoDxLabelDDL();
        IdoDxLabelDDL.Criteria labelCriteria = dxLabelDDL.createCriteria();
        labelCriteria.andPrdIdEqualTo(pId);
        List<IdoDxLabel> idoDxLabels = idoDxLabelMapper.selectByDDL(dxLabelDDL);

        HomeProductVO homeProductVO = HomeProductVO.builder()
                .attributes(idoDxAttributes)
                .labels(idoDxLabels)
                .links(idoDxLinks)
                .build();
        BeanUtils.copyProperties(idoDxProduct, homeProductVO);
        return homeProductVO;
    }

    @Override
    public int updateProduct(IdoDxProduct product) {
        return idoDxProductMapper.updateByPrimaryKeySelective(product);
    }

}
