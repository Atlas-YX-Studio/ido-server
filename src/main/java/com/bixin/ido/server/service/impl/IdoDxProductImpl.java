package com.bixin.ido.server.service.impl;

import com.bixin.ido.server.bean.DO.IdoDxAttribute;
import com.bixin.ido.server.bean.DO.IdoDxLabel;
import com.bixin.ido.server.bean.DO.IdoDxLink;
import com.bixin.ido.server.bean.DO.IdoDxProduct;
import com.bixin.ido.server.bean.vo.HomeProductVO;
import com.bixin.ido.server.core.mapper.IdoDxAttributeMapper;
import com.bixin.ido.server.core.mapper.IdoDxLabelMapper;
import com.bixin.ido.server.core.mapper.IdoDxLinkMapper;
import com.bixin.ido.server.core.mapper.IdoDxProductMapper;
import com.bixin.ido.server.core.wrapDDL.IdoDxAttributeDDL;
import com.bixin.ido.server.core.wrapDDL.IdoDxLabelDDL;
import com.bixin.ido.server.core.wrapDDL.IdoDxLinkDDL;
import com.bixin.ido.server.core.wrapDDL.IdoDxProductDDL;
import com.bixin.ido.server.enums.ProductState;
import com.bixin.ido.server.service.IIdoDxProductService;
import com.bixin.ido.server.utils.LocalDateTimeUtil;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author zhangcheng
 * create 2021-08-06 5:00 下午
 */
@Service
public class IdoDxProductImpl implements IIdoDxProductService {

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

        if (ProductState.INIT == productState) {
            criteria.andStartTimeGreaterThan(currentTime);
        } else if (ProductState.PROCESSING == productState) {
            criteria.andStartTimeLessThanOrEqualTo(currentTime)
                    .andEndTimeGreaterThan(currentTime);
        } else if (ProductState.FINISH == productState) {
            criteria.andEndTimeLessThanOrEqualTo(currentTime);
        }

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
                    .address(p.getAddress())
                    .assignmentEndTime(p.getAssignmentEndTime())
                    .assignmentStartTime(p.getAssignmentStartTime())
                    .baseCurrency(p.getBaseCurrency())
                    .createTime(p.getCreateTime())
                    .endTime(p.getEndTime())
                    .currency(p.getCurrency())
                    .currencyTotal(p.getCurrencyTotal())
                    .icon(p.getIcon())
                    .id(p.getId())
                    .lockStartTime(p.getLockStartTime())
                    .lockEndTime(p.getLockEndTime())
                    .payStartTime(p.getPayStartTime())
                    .payEndTime(p.getPayEndTime())
                    .pledgeStartTime(p.getPledgeStartTime())
                    .pledgeEndTime(p.getPledgeEndTime())
                    .pledgeTotal(p.getPledgeTotal())
                    .prdDesc(p.getPrdDesc())
                    .prdName(p.getPrdName())
                    .prdDescEn(p.getPrdDescEn())
                    .raiseTotal(p.getRaiseTotal())
                    .rate(p.getRate())
                    .ruleDesc(p.getRuleDesc())
                    .ruleDescEn(p.getRuleDescEn())
                    .startTime(p.getStartTime())
                    .updateTime(p.getUpdateTime())
                    .state(p.getState())
                    .build();
            homeProductVOS.add(homeProductVO);
        }
        return homeProductVOS;
    }

    @Override
    public IdoDxProduct getProduct(long pId){
       return idoDxProductMapper.selectByPrimaryKey(pId);
    }

}
