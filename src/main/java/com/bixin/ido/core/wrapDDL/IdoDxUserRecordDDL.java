package com.bixin.ido.core.wrapDDL;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class IdoDxUserRecordDDL {
    protected String orderByClause;

    protected boolean distinct;

    protected List<Criteria> oredCriteria;

    public IdoDxUserRecordDDL() {
        oredCriteria = new ArrayList<>();
    }

    public void setOrderByClause(String orderByClause) {
        this.orderByClause = orderByClause;
    }

    public String getOrderByClause() {
        return orderByClause;
    }

    public void setDistinct(boolean distinct) {
        this.distinct = distinct;
    }

    public boolean isDistinct() {
        return distinct;
    }

    public List<Criteria> getOredCriteria() {
        return oredCriteria;
    }

    public void or(Criteria criteria) {
        oredCriteria.add(criteria);
    }

    public Criteria or() {
        Criteria criteria = createCriteriaInternal();
        oredCriteria.add(criteria);
        return criteria;
    }

    public Criteria createCriteria() {
        Criteria criteria = createCriteriaInternal();
        if (oredCriteria.size() == 0) {
            oredCriteria.add(criteria);
        }
        return criteria;
    }

    protected Criteria createCriteriaInternal() {
        Criteria criteria = new Criteria();
        return criteria;
    }

    public void clear() {
        oredCriteria.clear();
        orderByClause = null;
        distinct = false;
    }

    protected abstract static class GeneratedCriteria {
        protected List<Criterion> criteria;

        protected GeneratedCriteria() {
            super();
            criteria = new ArrayList<>();
        }

        public boolean isValid() {
            return criteria.size() > 0;
        }

        public List<Criterion> getAllCriteria() {
            return criteria;
        }

        public List<Criterion> getCriteria() {
            return criteria;
        }

        protected void addCriterion(String condition) {
            if (condition == null) {
                throw new RuntimeException("Value for condition cannot be null");
            }
            criteria.add(new Criterion(condition));
        }

        protected void addCriterion(String condition, Object value, String property) {
            if (value == null) {
                throw new RuntimeException("Value for " + property + " cannot be null");
            }
            criteria.add(new Criterion(condition, value));
        }

        protected void addCriterion(String condition, Object value1, Object value2, String property) {
            if (value1 == null || value2 == null) {
                throw new RuntimeException("Between values for " + property + " cannot be null");
            }
            criteria.add(new Criterion(condition, value1, value2));
        }

        public Criteria andIdIsNull() {
            addCriterion("id is null");
            return (Criteria) this;
        }

        public Criteria andIdIsNotNull() {
            addCriterion("id is not null");
            return (Criteria) this;
        }

        public Criteria andIdEqualTo(Long value) {
            addCriterion("id =", value, "id");
            return (Criteria) this;
        }

        public Criteria andIdNotEqualTo(Long value) {
            addCriterion("id <>", value, "id");
            return (Criteria) this;
        }

        public Criteria andIdGreaterThan(Long value) {
            addCriterion("id >", value, "id");
            return (Criteria) this;
        }

        public Criteria andIdGreaterThanOrEqualTo(Long value) {
            addCriterion("id >=", value, "id");
            return (Criteria) this;
        }

        public Criteria andIdLessThan(Long value) {
            addCriterion("id <", value, "id");
            return (Criteria) this;
        }

        public Criteria andIdLessThanOrEqualTo(Long value) {
            addCriterion("id <=", value, "id");
            return (Criteria) this;
        }

        public Criteria andIdIn(List<Long> values) {
            addCriterion("id in", values, "id");
            return (Criteria) this;
        }

        public Criteria andIdNotIn(List<Long> values) {
            addCriterion("id not in", values, "id");
            return (Criteria) this;
        }

        public Criteria andIdBetween(Long value1, Long value2) {
            addCriterion("id between", value1, value2, "id");
            return (Criteria) this;
        }

        public Criteria andIdNotBetween(Long value1, Long value2) {
            addCriterion("id not between", value1, value2, "id");
            return (Criteria) this;
        }

        public Criteria andPrdAddressIsNull() {
            addCriterion("prdAddress is null");
            return (Criteria) this;
        }

        public Criteria andPrdAddressIsNotNull() {
            addCriterion("prdAddress is not null");
            return (Criteria) this;
        }

        public Criteria andPrdAddressEqualTo(String value) {
            addCriterion("prdAddress =", value, "prdAddress");
            return (Criteria) this;
        }

        public Criteria andPrdAddressNotEqualTo(String value) {
            addCriterion("prdAddress <>", value, "prdAddress");
            return (Criteria) this;
        }

        public Criteria andPrdAddressGreaterThan(String value) {
            addCriterion("prdAddress >", value, "prdAddress");
            return (Criteria) this;
        }

        public Criteria andPrdAddressGreaterThanOrEqualTo(String value) {
            addCriterion("prdAddress >=", value, "prdAddress");
            return (Criteria) this;
        }

        public Criteria andPrdAddressLessThan(String value) {
            addCriterion("prdAddress <", value, "prdAddress");
            return (Criteria) this;
        }

        public Criteria andPrdAddressLessThanOrEqualTo(String value) {
            addCriterion("prdAddress <=", value, "prdAddress");
            return (Criteria) this;
        }

        public Criteria andPrdAddressLike(String value) {
            addCriterion("prdAddress like", value, "prdAddress");
            return (Criteria) this;
        }

        public Criteria andPrdAddressNotLike(String value) {
            addCriterion("prdAddress not like", value, "prdAddress");
            return (Criteria) this;
        }

        public Criteria andPrdAddressIn(List<String> values) {
            addCriterion("prdAddress in", values, "prdAddress");
            return (Criteria) this;
        }

        public Criteria andPrdAddressNotIn(List<String> values) {
            addCriterion("prdAddress not in", values, "prdAddress");
            return (Criteria) this;
        }

        public Criteria andPrdAddressBetween(String value1, String value2) {
            addCriterion("prdAddress between", value1, value2, "prdAddress");
            return (Criteria) this;
        }

        public Criteria andPrdAddressNotBetween(String value1, String value2) {
            addCriterion("prdAddress not between", value1, value2, "prdAddress");
            return (Criteria) this;
        }

        public Criteria andUserAddressIsNull() {
            addCriterion("userAddress is null");
            return (Criteria) this;
        }

        public Criteria andUserAddressIsNotNull() {
            addCriterion("userAddress is not null");
            return (Criteria) this;
        }

        public Criteria andUserAddressEqualTo(String value) {
            addCriterion("userAddress =", value, "userAddress");
            return (Criteria) this;
        }

        public Criteria andUserAddressNotEqualTo(String value) {
            addCriterion("userAddress <>", value, "userAddress");
            return (Criteria) this;
        }

        public Criteria andUserAddressGreaterThan(String value) {
            addCriterion("userAddress >", value, "userAddress");
            return (Criteria) this;
        }

        public Criteria andUserAddressGreaterThanOrEqualTo(String value) {
            addCriterion("userAddress >=", value, "userAddress");
            return (Criteria) this;
        }

        public Criteria andUserAddressLessThan(String value) {
            addCriterion("userAddress <", value, "userAddress");
            return (Criteria) this;
        }

        public Criteria andUserAddressLessThanOrEqualTo(String value) {
            addCriterion("userAddress <=", value, "userAddress");
            return (Criteria) this;
        }

        public Criteria andUserAddressLike(String value) {
            addCriterion("userAddress like", value, "userAddress");
            return (Criteria) this;
        }

        public Criteria andUserAddressNotLike(String value) {
            addCriterion("userAddress not like", value, "userAddress");
            return (Criteria) this;
        }

        public Criteria andUserAddressIn(List<String> values) {
            addCriterion("userAddress in", values, "userAddress");
            return (Criteria) this;
        }

        public Criteria andUserAddressNotIn(List<String> values) {
            addCriterion("userAddress not in", values, "userAddress");
            return (Criteria) this;
        }

        public Criteria andUserAddressBetween(String value1, String value2) {
            addCriterion("userAddress between", value1, value2, "userAddress");
            return (Criteria) this;
        }

        public Criteria andUserAddressNotBetween(String value1, String value2) {
            addCriterion("userAddress not between", value1, value2, "userAddress");
            return (Criteria) this;
        }

        public Criteria andAmountIsNull() {
            addCriterion("amount is null");
            return (Criteria) this;
        }

        public Criteria andAmountIsNotNull() {
            addCriterion("amount is not null");
            return (Criteria) this;
        }

        public Criteria andAmountEqualTo(BigDecimal value) {
            addCriterion("amount =", value, "amount");
            return (Criteria) this;
        }

        public Criteria andAmountNotEqualTo(BigDecimal value) {
            addCriterion("amount <>", value, "amount");
            return (Criteria) this;
        }

        public Criteria andAmountGreaterThan(BigDecimal value) {
            addCriterion("amount >", value, "amount");
            return (Criteria) this;
        }

        public Criteria andAmountGreaterThanOrEqualTo(BigDecimal value) {
            addCriterion("amount >=", value, "amount");
            return (Criteria) this;
        }

        public Criteria andAmountLessThan(BigDecimal value) {
            addCriterion("amount <", value, "amount");
            return (Criteria) this;
        }

        public Criteria andAmountLessThanOrEqualTo(BigDecimal value) {
            addCriterion("amount <=", value, "amount");
            return (Criteria) this;
        }

        public Criteria andAmountIn(List<BigDecimal> values) {
            addCriterion("amount in", values, "amount");
            return (Criteria) this;
        }

        public Criteria andAmountNotIn(List<BigDecimal> values) {
            addCriterion("amount not in", values, "amount");
            return (Criteria) this;
        }

        public Criteria andAmountBetween(BigDecimal value1, BigDecimal value2) {
            addCriterion("amount between", value1, value2, "amount");
            return (Criteria) this;
        }

        public Criteria andAmountNotBetween(BigDecimal value1, BigDecimal value2) {
            addCriterion("amount not between", value1, value2, "amount");
            return (Criteria) this;
        }

        public Criteria andGasCostIsNull() {
            addCriterion("gasCost is null");
            return (Criteria) this;
        }

        public Criteria andGasCostIsNotNull() {
            addCriterion("gasCost is not null");
            return (Criteria) this;
        }

        public Criteria andGasCostEqualTo(BigDecimal value) {
            addCriterion("gasCost =", value, "gasCost");
            return (Criteria) this;
        }

        public Criteria andGasCostNotEqualTo(BigDecimal value) {
            addCriterion("gasCost <>", value, "gasCost");
            return (Criteria) this;
        }

        public Criteria andGasCostGreaterThan(BigDecimal value) {
            addCriterion("gasCost >", value, "gasCost");
            return (Criteria) this;
        }

        public Criteria andGasCostGreaterThanOrEqualTo(BigDecimal value) {
            addCriterion("gasCost >=", value, "gasCost");
            return (Criteria) this;
        }

        public Criteria andGasCostLessThan(BigDecimal value) {
            addCriterion("gasCost <", value, "gasCost");
            return (Criteria) this;
        }

        public Criteria andGasCostLessThanOrEqualTo(BigDecimal value) {
            addCriterion("gasCost <=", value, "gasCost");
            return (Criteria) this;
        }

        public Criteria andGasCostIn(List<BigDecimal> values) {
            addCriterion("gasCost in", values, "gasCost");
            return (Criteria) this;
        }

        public Criteria andGasCostNotIn(List<BigDecimal> values) {
            addCriterion("gasCost not in", values, "gasCost");
            return (Criteria) this;
        }

        public Criteria andGasCostBetween(BigDecimal value1, BigDecimal value2) {
            addCriterion("gasCost between", value1, value2, "gasCost");
            return (Criteria) this;
        }

        public Criteria andGasCostNotBetween(BigDecimal value1, BigDecimal value2) {
            addCriterion("gasCost not between", value1, value2, "gasCost");
            return (Criteria) this;
        }

        public Criteria andTokenAmountIsNull() {
            addCriterion("tokenAmount is null");
            return (Criteria) this;
        }

        public Criteria andTokenAmountIsNotNull() {
            addCriterion("tokenAmount is not null");
            return (Criteria) this;
        }

        public Criteria andTokenAmountEqualTo(BigDecimal value) {
            addCriterion("tokenAmount =", value, "tokenAmount");
            return (Criteria) this;
        }

        public Criteria andTokenAmountNotEqualTo(BigDecimal value) {
            addCriterion("tokenAmount <>", value, "tokenAmount");
            return (Criteria) this;
        }

        public Criteria andTokenAmountGreaterThan(BigDecimal value) {
            addCriterion("tokenAmount >", value, "tokenAmount");
            return (Criteria) this;
        }

        public Criteria andTokenAmountGreaterThanOrEqualTo(BigDecimal value) {
            addCriterion("tokenAmount >=", value, "tokenAmount");
            return (Criteria) this;
        }

        public Criteria andTokenAmountLessThan(BigDecimal value) {
            addCriterion("tokenAmount <", value, "tokenAmount");
            return (Criteria) this;
        }

        public Criteria andTokenAmountLessThanOrEqualTo(BigDecimal value) {
            addCriterion("tokenAmount <=", value, "tokenAmount");
            return (Criteria) this;
        }

        public Criteria andTokenAmountIn(List<BigDecimal> values) {
            addCriterion("tokenAmount in", values, "tokenAmount");
            return (Criteria) this;
        }

        public Criteria andTokenAmountNotIn(List<BigDecimal> values) {
            addCriterion("tokenAmount not in", values, "tokenAmount");
            return (Criteria) this;
        }

        public Criteria andTokenAmountBetween(BigDecimal value1, BigDecimal value2) {
            addCriterion("tokenAmount between", value1, value2, "tokenAmount");
            return (Criteria) this;
        }

        public Criteria andTokenAmountNotBetween(BigDecimal value1, BigDecimal value2) {
            addCriterion("tokenAmount not between", value1, value2, "tokenAmount");
            return (Criteria) this;
        }

        public Criteria andCurrencyIsNull() {
            addCriterion("currency is null");
            return (Criteria) this;
        }

        public Criteria andCurrencyIsNotNull() {
            addCriterion("currency is not null");
            return (Criteria) this;
        }

        public Criteria andCurrencyEqualTo(String value) {
            addCriterion("currency =", value, "currency");
            return (Criteria) this;
        }

        public Criteria andCurrencyNotEqualTo(String value) {
            addCriterion("currency <>", value, "currency");
            return (Criteria) this;
        }

        public Criteria andCurrencyGreaterThan(String value) {
            addCriterion("currency >", value, "currency");
            return (Criteria) this;
        }

        public Criteria andCurrencyGreaterThanOrEqualTo(String value) {
            addCriterion("currency >=", value, "currency");
            return (Criteria) this;
        }

        public Criteria andCurrencyLessThan(String value) {
            addCriterion("currency <", value, "currency");
            return (Criteria) this;
        }

        public Criteria andCurrencyLessThanOrEqualTo(String value) {
            addCriterion("currency <=", value, "currency");
            return (Criteria) this;
        }

        public Criteria andCurrencyLike(String value) {
            addCriterion("currency like", value, "currency");
            return (Criteria) this;
        }

        public Criteria andCurrencyNotLike(String value) {
            addCriterion("currency not like", value, "currency");
            return (Criteria) this;
        }

        public Criteria andCurrencyIn(List<String> values) {
            addCriterion("currency in", values, "currency");
            return (Criteria) this;
        }

        public Criteria andCurrencyNotIn(List<String> values) {
            addCriterion("currency not in", values, "currency");
            return (Criteria) this;
        }

        public Criteria andCurrencyBetween(String value1, String value2) {
            addCriterion("currency between", value1, value2, "currency");
            return (Criteria) this;
        }

        public Criteria andCurrencyNotBetween(String value1, String value2) {
            addCriterion("currency not between", value1, value2, "currency");
            return (Criteria) this;
        }

        public Criteria andExtInfoIsNull() {
            addCriterion("extInfo is null");
            return (Criteria) this;
        }

        public Criteria andExtInfoIsNotNull() {
            addCriterion("extInfo is not null");
            return (Criteria) this;
        }

        public Criteria andExtInfoEqualTo(String value) {
            addCriterion("extInfo =", value, "extInfo");
            return (Criteria) this;
        }

        public Criteria andExtInfoNotEqualTo(String value) {
            addCriterion("extInfo <>", value, "extInfo");
            return (Criteria) this;
        }

        public Criteria andExtInfoGreaterThan(String value) {
            addCriterion("extInfo >", value, "extInfo");
            return (Criteria) this;
        }

        public Criteria andExtInfoGreaterThanOrEqualTo(String value) {
            addCriterion("extInfo >=", value, "extInfo");
            return (Criteria) this;
        }

        public Criteria andExtInfoLessThan(String value) {
            addCriterion("extInfo <", value, "extInfo");
            return (Criteria) this;
        }

        public Criteria andExtInfoLessThanOrEqualTo(String value) {
            addCriterion("extInfo <=", value, "extInfo");
            return (Criteria) this;
        }

        public Criteria andExtInfoLike(String value) {
            addCriterion("extInfo like", value, "extInfo");
            return (Criteria) this;
        }

        public Criteria andExtInfoNotLike(String value) {
            addCriterion("extInfo not like", value, "extInfo");
            return (Criteria) this;
        }

        public Criteria andExtInfoIn(List<String> values) {
            addCriterion("extInfo in", values, "extInfo");
            return (Criteria) this;
        }

        public Criteria andExtInfoNotIn(List<String> values) {
            addCriterion("extInfo not in", values, "extInfo");
            return (Criteria) this;
        }

        public Criteria andExtInfoBetween(String value1, String value2) {
            addCriterion("extInfo between", value1, value2, "extInfo");
            return (Criteria) this;
        }

        public Criteria andExtInfoNotBetween(String value1, String value2) {
            addCriterion("extInfo not between", value1, value2, "extInfo");
            return (Criteria) this;
        }

        public Criteria andTokenVersionIsNull() {
            addCriterion("tokenVersion is null");
            return (Criteria) this;
        }

        public Criteria andTokenVersionIsNotNull() {
            addCriterion("tokenVersion is not null");
            return (Criteria) this;
        }

        public Criteria andTokenVersionEqualTo(Short value) {
            addCriterion("tokenVersion =", value, "tokenVersion");
            return (Criteria) this;
        }

        public Criteria andTokenVersionNotEqualTo(Short value) {
            addCriterion("tokenVersion <>", value, "tokenVersion");
            return (Criteria) this;
        }

        public Criteria andTokenVersionGreaterThan(Short value) {
            addCriterion("tokenVersion >", value, "tokenVersion");
            return (Criteria) this;
        }

        public Criteria andTokenVersionGreaterThanOrEqualTo(Short value) {
            addCriterion("tokenVersion >=", value, "tokenVersion");
            return (Criteria) this;
        }

        public Criteria andTokenVersionLessThan(Short value) {
            addCriterion("tokenVersion <", value, "tokenVersion");
            return (Criteria) this;
        }

        public Criteria andTokenVersionLessThanOrEqualTo(Short value) {
            addCriterion("tokenVersion <=", value, "tokenVersion");
            return (Criteria) this;
        }

        public Criteria andTokenVersionIn(List<Short> values) {
            addCriterion("tokenVersion in", values, "tokenVersion");
            return (Criteria) this;
        }

        public Criteria andTokenVersionNotIn(List<Short> values) {
            addCriterion("tokenVersion not in", values, "tokenVersion");
            return (Criteria) this;
        }

        public Criteria andTokenVersionBetween(Short value1, Short value2) {
            addCriterion("tokenVersion between", value1, value2, "tokenVersion");
            return (Criteria) this;
        }

        public Criteria andTokenVersionNotBetween(Short value1, Short value2) {
            addCriterion("tokenVersion not between", value1, value2, "tokenVersion");
            return (Criteria) this;
        }

        public Criteria andCreateTimeIsNull() {
            addCriterion("createTime is null");
            return (Criteria) this;
        }

        public Criteria andCreateTimeIsNotNull() {
            addCriterion("createTime is not null");
            return (Criteria) this;
        }

        public Criteria andCreateTimeEqualTo(Long value) {
            addCriterion("createTime =", value, "createTime");
            return (Criteria) this;
        }

        public Criteria andCreateTimeNotEqualTo(Long value) {
            addCriterion("createTime <>", value, "createTime");
            return (Criteria) this;
        }

        public Criteria andCreateTimeGreaterThan(Long value) {
            addCriterion("createTime >", value, "createTime");
            return (Criteria) this;
        }

        public Criteria andCreateTimeGreaterThanOrEqualTo(Long value) {
            addCriterion("createTime >=", value, "createTime");
            return (Criteria) this;
        }

        public Criteria andCreateTimeLessThan(Long value) {
            addCriterion("createTime <", value, "createTime");
            return (Criteria) this;
        }

        public Criteria andCreateTimeLessThanOrEqualTo(Long value) {
            addCriterion("createTime <=", value, "createTime");
            return (Criteria) this;
        }

        public Criteria andCreateTimeIn(List<Long> values) {
            addCriterion("createTime in", values, "createTime");
            return (Criteria) this;
        }

        public Criteria andCreateTimeNotIn(List<Long> values) {
            addCriterion("createTime not in", values, "createTime");
            return (Criteria) this;
        }

        public Criteria andCreateTimeBetween(Long value1, Long value2) {
            addCriterion("createTime between", value1, value2, "createTime");
            return (Criteria) this;
        }

        public Criteria andCreateTimeNotBetween(Long value1, Long value2) {
            addCriterion("createTime not between", value1, value2, "createTime");
            return (Criteria) this;
        }

        public Criteria andUpdateTimeIsNull() {
            addCriterion("updateTime is null");
            return (Criteria) this;
        }

        public Criteria andUpdateTimeIsNotNull() {
            addCriterion("updateTime is not null");
            return (Criteria) this;
        }

        public Criteria andUpdateTimeEqualTo(Long value) {
            addCriterion("updateTime =", value, "updateTime");
            return (Criteria) this;
        }

        public Criteria andUpdateTimeNotEqualTo(Long value) {
            addCriterion("updateTime <>", value, "updateTime");
            return (Criteria) this;
        }

        public Criteria andUpdateTimeGreaterThan(Long value) {
            addCriterion("updateTime >", value, "updateTime");
            return (Criteria) this;
        }

        public Criteria andUpdateTimeGreaterThanOrEqualTo(Long value) {
            addCriterion("updateTime >=", value, "updateTime");
            return (Criteria) this;
        }

        public Criteria andUpdateTimeLessThan(Long value) {
            addCriterion("updateTime <", value, "updateTime");
            return (Criteria) this;
        }

        public Criteria andUpdateTimeLessThanOrEqualTo(Long value) {
            addCriterion("updateTime <=", value, "updateTime");
            return (Criteria) this;
        }

        public Criteria andUpdateTimeIn(List<Long> values) {
            addCriterion("updateTime in", values, "updateTime");
            return (Criteria) this;
        }

        public Criteria andUpdateTimeNotIn(List<Long> values) {
            addCriterion("updateTime not in", values, "updateTime");
            return (Criteria) this;
        }

        public Criteria andUpdateTimeBetween(Long value1, Long value2) {
            addCriterion("updateTime between", value1, value2, "updateTime");
            return (Criteria) this;
        }

        public Criteria andUpdateTimeNotBetween(Long value1, Long value2) {
            addCriterion("updateTime not between", value1, value2, "updateTime");
            return (Criteria) this;
        }
    }

    public static class Criteria extends GeneratedCriteria {
        protected Criteria() {
            super();
        }
    }

    public static class Criterion {
        private String condition;

        private Object value;

        private Object secondValue;

        private boolean noValue;

        private boolean singleValue;

        private boolean betweenValue;

        private boolean listValue;

        private String typeHandler;

        public String getCondition() {
            return condition;
        }

        public Object getValue() {
            return value;
        }

        public Object getSecondValue() {
            return secondValue;
        }

        public boolean isNoValue() {
            return noValue;
        }

        public boolean isSingleValue() {
            return singleValue;
        }

        public boolean isBetweenValue() {
            return betweenValue;
        }

        public boolean isListValue() {
            return listValue;
        }

        public String getTypeHandler() {
            return typeHandler;
        }

        protected Criterion(String condition) {
            super();
            this.condition = condition;
            this.typeHandler = null;
            this.noValue = true;
        }

        protected Criterion(String condition, Object value, String typeHandler) {
            super();
            this.condition = condition;
            this.value = value;
            this.typeHandler = typeHandler;
            if (value instanceof List<?>) {
                this.listValue = true;
            } else {
                this.singleValue = true;
            }
        }

        protected Criterion(String condition, Object value) {
            this(condition, value, null);
        }

        protected Criterion(String condition, Object value, Object secondValue, String typeHandler) {
            super();
            this.condition = condition;
            this.value = value;
            this.secondValue = secondValue;
            this.typeHandler = typeHandler;
            this.betweenValue = true;
        }

        protected Criterion(String condition, Object value, Object secondValue) {
            this(condition, value, secondValue, null);
        }
    }
}