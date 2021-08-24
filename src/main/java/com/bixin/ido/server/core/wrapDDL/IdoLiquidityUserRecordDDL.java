package com.bixin.ido.server.core.wrapDDL;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class IdoLiquidityUserRecordDDL {
    protected String orderByClause;

    protected boolean distinct;

    protected List<Criteria> oredCriteria;

    public IdoLiquidityUserRecordDDL() {
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

        public Criteria andTokenCodeXIsNull() {
            addCriterion("tokenCodeX is null");
            return (Criteria) this;
        }

        public Criteria andTokenCodeXIsNotNull() {
            addCriterion("tokenCodeX is not null");
            return (Criteria) this;
        }

        public Criteria andTokenCodeXEqualTo(String value) {
            addCriterion("tokenCodeX =", value, "tokenCodeX");
            return (Criteria) this;
        }

        public Criteria andTokenCodeXNotEqualTo(String value) {
            addCriterion("tokenCodeX <>", value, "tokenCodeX");
            return (Criteria) this;
        }

        public Criteria andTokenCodeXGreaterThan(String value) {
            addCriterion("tokenCodeX >", value, "tokenCodeX");
            return (Criteria) this;
        }

        public Criteria andTokenCodeXGreaterThanOrEqualTo(String value) {
            addCriterion("tokenCodeX >=", value, "tokenCodeX");
            return (Criteria) this;
        }

        public Criteria andTokenCodeXLessThan(String value) {
            addCriterion("tokenCodeX <", value, "tokenCodeX");
            return (Criteria) this;
        }

        public Criteria andTokenCodeXLessThanOrEqualTo(String value) {
            addCriterion("tokenCodeX <=", value, "tokenCodeX");
            return (Criteria) this;
        }

        public Criteria andTokenCodeXLike(String value) {
            addCriterion("tokenCodeX like", value, "tokenCodeX");
            return (Criteria) this;
        }

        public Criteria andTokenCodeXNotLike(String value) {
            addCriterion("tokenCodeX not like", value, "tokenCodeX");
            return (Criteria) this;
        }

        public Criteria andTokenCodeXIn(List<String> values) {
            addCriterion("tokenCodeX in", values, "tokenCodeX");
            return (Criteria) this;
        }

        public Criteria andTokenCodeXNotIn(List<String> values) {
            addCriterion("tokenCodeX not in", values, "tokenCodeX");
            return (Criteria) this;
        }

        public Criteria andTokenCodeXBetween(String value1, String value2) {
            addCriterion("tokenCodeX between", value1, value2, "tokenCodeX");
            return (Criteria) this;
        }

        public Criteria andTokenCodeXNotBetween(String value1, String value2) {
            addCriterion("tokenCodeX not between", value1, value2, "tokenCodeX");
            return (Criteria) this;
        }

        public Criteria andTokenCodeYIsNull() {
            addCriterion("tokenCodeY is null");
            return (Criteria) this;
        }

        public Criteria andTokenCodeYIsNotNull() {
            addCriterion("tokenCodeY is not null");
            return (Criteria) this;
        }

        public Criteria andTokenCodeYEqualTo(String value) {
            addCriterion("tokenCodeY =", value, "tokenCodeY");
            return (Criteria) this;
        }

        public Criteria andTokenCodeYNotEqualTo(String value) {
            addCriterion("tokenCodeY <>", value, "tokenCodeY");
            return (Criteria) this;
        }

        public Criteria andTokenCodeYGreaterThan(String value) {
            addCriterion("tokenCodeY >", value, "tokenCodeY");
            return (Criteria) this;
        }

        public Criteria andTokenCodeYGreaterThanOrEqualTo(String value) {
            addCriterion("tokenCodeY >=", value, "tokenCodeY");
            return (Criteria) this;
        }

        public Criteria andTokenCodeYLessThan(String value) {
            addCriterion("tokenCodeY <", value, "tokenCodeY");
            return (Criteria) this;
        }

        public Criteria andTokenCodeYLessThanOrEqualTo(String value) {
            addCriterion("tokenCodeY <=", value, "tokenCodeY");
            return (Criteria) this;
        }

        public Criteria andTokenCodeYLike(String value) {
            addCriterion("tokenCodeY like", value, "tokenCodeY");
            return (Criteria) this;
        }

        public Criteria andTokenCodeYNotLike(String value) {
            addCriterion("tokenCodeY not like", value, "tokenCodeY");
            return (Criteria) this;
        }

        public Criteria andTokenCodeYIn(List<String> values) {
            addCriterion("tokenCodeY in", values, "tokenCodeY");
            return (Criteria) this;
        }

        public Criteria andTokenCodeYNotIn(List<String> values) {
            addCriterion("tokenCodeY not in", values, "tokenCodeY");
            return (Criteria) this;
        }

        public Criteria andTokenCodeYBetween(String value1, String value2) {
            addCriterion("tokenCodeY between", value1, value2, "tokenCodeY");
            return (Criteria) this;
        }

        public Criteria andTokenCodeYNotBetween(String value1, String value2) {
            addCriterion("tokenCodeY not between", value1, value2, "tokenCodeY");
            return (Criteria) this;
        }

        public Criteria andDirectionIsNull() {
            addCriterion("direction is null");
            return (Criteria) this;
        }

        public Criteria andDirectionIsNotNull() {
            addCriterion("direction is not null");
            return (Criteria) this;
        }

        public Criteria andDirectionEqualTo(Short value) {
            addCriterion("direction =", value, "direction");
            return (Criteria) this;
        }

        public Criteria andDirectionNotEqualTo(Short value) {
            addCriterion("direction <>", value, "direction");
            return (Criteria) this;
        }

        public Criteria andDirectionGreaterThan(Short value) {
            addCriterion("direction >", value, "direction");
            return (Criteria) this;
        }

        public Criteria andDirectionGreaterThanOrEqualTo(Short value) {
            addCriterion("direction >=", value, "direction");
            return (Criteria) this;
        }

        public Criteria andDirectionLessThan(Short value) {
            addCriterion("direction <", value, "direction");
            return (Criteria) this;
        }

        public Criteria andDirectionLessThanOrEqualTo(Short value) {
            addCriterion("direction <=", value, "direction");
            return (Criteria) this;
        }

        public Criteria andDirectionIn(List<Short> values) {
            addCriterion("direction in", values, "direction");
            return (Criteria) this;
        }

        public Criteria andDirectionNotIn(List<Short> values) {
            addCriterion("direction not in", values, "direction");
            return (Criteria) this;
        }

        public Criteria andDirectionBetween(Short value1, Short value2) {
            addCriterion("direction between", value1, value2, "direction");
            return (Criteria) this;
        }

        public Criteria andDirectionNotBetween(Short value1, Short value2) {
            addCriterion("direction not between", value1, value2, "direction");
            return (Criteria) this;
        }

        public Criteria andAmountXIsNull() {
            addCriterion("amountX is null");
            return (Criteria) this;
        }

        public Criteria andAmountXIsNotNull() {
            addCriterion("amountX is not null");
            return (Criteria) this;
        }

        public Criteria andAmountXEqualTo(BigDecimal value) {
            addCriterion("amountX =", value, "amountX");
            return (Criteria) this;
        }

        public Criteria andAmountXNotEqualTo(BigDecimal value) {
            addCriterion("amountX <>", value, "amountX");
            return (Criteria) this;
        }

        public Criteria andAmountXGreaterThan(BigDecimal value) {
            addCriterion("amountX >", value, "amountX");
            return (Criteria) this;
        }

        public Criteria andAmountXGreaterThanOrEqualTo(BigDecimal value) {
            addCriterion("amountX >=", value, "amountX");
            return (Criteria) this;
        }

        public Criteria andAmountXLessThan(BigDecimal value) {
            addCriterion("amountX <", value, "amountX");
            return (Criteria) this;
        }

        public Criteria andAmountXLessThanOrEqualTo(BigDecimal value) {
            addCriterion("amountX <=", value, "amountX");
            return (Criteria) this;
        }

        public Criteria andAmountXIn(List<BigDecimal> values) {
            addCriterion("amountX in", values, "amountX");
            return (Criteria) this;
        }

        public Criteria andAmountXNotIn(List<BigDecimal> values) {
            addCriterion("amountX not in", values, "amountX");
            return (Criteria) this;
        }

        public Criteria andAmountXBetween(BigDecimal value1, BigDecimal value2) {
            addCriterion("amountX between", value1, value2, "amountX");
            return (Criteria) this;
        }

        public Criteria andAmountXNotBetween(BigDecimal value1, BigDecimal value2) {
            addCriterion("amountX not between", value1, value2, "amountX");
            return (Criteria) this;
        }

        public Criteria andAmountYIsNull() {
            addCriterion("amountY is null");
            return (Criteria) this;
        }

        public Criteria andAmountYIsNotNull() {
            addCriterion("amountY is not null");
            return (Criteria) this;
        }

        public Criteria andAmountYEqualTo(BigDecimal value) {
            addCriterion("amountY =", value, "amountY");
            return (Criteria) this;
        }

        public Criteria andAmountYNotEqualTo(BigDecimal value) {
            addCriterion("amountY <>", value, "amountY");
            return (Criteria) this;
        }

        public Criteria andAmountYGreaterThan(BigDecimal value) {
            addCriterion("amountY >", value, "amountY");
            return (Criteria) this;
        }

        public Criteria andAmountYGreaterThanOrEqualTo(BigDecimal value) {
            addCriterion("amountY >=", value, "amountY");
            return (Criteria) this;
        }

        public Criteria andAmountYLessThan(BigDecimal value) {
            addCriterion("amountY <", value, "amountY");
            return (Criteria) this;
        }

        public Criteria andAmountYLessThanOrEqualTo(BigDecimal value) {
            addCriterion("amountY <=", value, "amountY");
            return (Criteria) this;
        }

        public Criteria andAmountYIn(List<BigDecimal> values) {
            addCriterion("amountY in", values, "amountY");
            return (Criteria) this;
        }

        public Criteria andAmountYNotIn(List<BigDecimal> values) {
            addCriterion("amountY not in", values, "amountY");
            return (Criteria) this;
        }

        public Criteria andAmountYBetween(BigDecimal value1, BigDecimal value2) {
            addCriterion("amountY between", value1, value2, "amountY");
            return (Criteria) this;
        }

        public Criteria andAmountYNotBetween(BigDecimal value1, BigDecimal value2) {
            addCriterion("amountY not between", value1, value2, "amountY");
            return (Criteria) this;
        }

        public Criteria andReserveAmountXIsNull() {
            addCriterion("reserveAmountX is null");
            return (Criteria) this;
        }

        public Criteria andReserveAmountXIsNotNull() {
            addCriterion("reserveAmountX is not null");
            return (Criteria) this;
        }

        public Criteria andReserveAmountXEqualTo(BigDecimal value) {
            addCriterion("reserveAmountX =", value, "reserveAmountX");
            return (Criteria) this;
        }

        public Criteria andReserveAmountXNotEqualTo(BigDecimal value) {
            addCriterion("reserveAmountX <>", value, "reserveAmountX");
            return (Criteria) this;
        }

        public Criteria andReserveAmountXGreaterThan(BigDecimal value) {
            addCriterion("reserveAmountX >", value, "reserveAmountX");
            return (Criteria) this;
        }

        public Criteria andReserveAmountXGreaterThanOrEqualTo(BigDecimal value) {
            addCriterion("reserveAmountX >=", value, "reserveAmountX");
            return (Criteria) this;
        }

        public Criteria andReserveAmountXLessThan(BigDecimal value) {
            addCriterion("reserveAmountX <", value, "reserveAmountX");
            return (Criteria) this;
        }

        public Criteria andReserveAmountXLessThanOrEqualTo(BigDecimal value) {
            addCriterion("reserveAmountX <=", value, "reserveAmountX");
            return (Criteria) this;
        }

        public Criteria andReserveAmountXIn(List<BigDecimal> values) {
            addCriterion("reserveAmountX in", values, "reserveAmountX");
            return (Criteria) this;
        }

        public Criteria andReserveAmountXNotIn(List<BigDecimal> values) {
            addCriterion("reserveAmountX not in", values, "reserveAmountX");
            return (Criteria) this;
        }

        public Criteria andReserveAmountXBetween(BigDecimal value1, BigDecimal value2) {
            addCriterion("reserveAmountX between", value1, value2, "reserveAmountX");
            return (Criteria) this;
        }

        public Criteria andReserveAmountXNotBetween(BigDecimal value1, BigDecimal value2) {
            addCriterion("reserveAmountX not between", value1, value2, "reserveAmountX");
            return (Criteria) this;
        }

        public Criteria andReserveAmountYIsNull() {
            addCriterion("reserveAmountY is null");
            return (Criteria) this;
        }

        public Criteria andReserveAmountYIsNotNull() {
            addCriterion("reserveAmountY is not null");
            return (Criteria) this;
        }

        public Criteria andReserveAmountYEqualTo(BigDecimal value) {
            addCriterion("reserveAmountY =", value, "reserveAmountY");
            return (Criteria) this;
        }

        public Criteria andReserveAmountYNotEqualTo(BigDecimal value) {
            addCriterion("reserveAmountY <>", value, "reserveAmountY");
            return (Criteria) this;
        }

        public Criteria andReserveAmountYGreaterThan(BigDecimal value) {
            addCriterion("reserveAmountY >", value, "reserveAmountY");
            return (Criteria) this;
        }

        public Criteria andReserveAmountYGreaterThanOrEqualTo(BigDecimal value) {
            addCriterion("reserveAmountY >=", value, "reserveAmountY");
            return (Criteria) this;
        }

        public Criteria andReserveAmountYLessThan(BigDecimal value) {
            addCriterion("reserveAmountY <", value, "reserveAmountY");
            return (Criteria) this;
        }

        public Criteria andReserveAmountYLessThanOrEqualTo(BigDecimal value) {
            addCriterion("reserveAmountY <=", value, "reserveAmountY");
            return (Criteria) this;
        }

        public Criteria andReserveAmountYIn(List<BigDecimal> values) {
            addCriterion("reserveAmountY in", values, "reserveAmountY");
            return (Criteria) this;
        }

        public Criteria andReserveAmountYNotIn(List<BigDecimal> values) {
            addCriterion("reserveAmountY not in", values, "reserveAmountY");
            return (Criteria) this;
        }

        public Criteria andReserveAmountYBetween(BigDecimal value1, BigDecimal value2) {
            addCriterion("reserveAmountY between", value1, value2, "reserveAmountY");
            return (Criteria) this;
        }

        public Criteria andReserveAmountYNotBetween(BigDecimal value1, BigDecimal value2) {
            addCriterion("reserveAmountY not between", value1, value2, "reserveAmountY");
            return (Criteria) this;
        }

        public Criteria andLiquidityTimeIsNull() {
            addCriterion("liquidityTime is null");
            return (Criteria) this;
        }

        public Criteria andLiquidityTimeIsNotNull() {
            addCriterion("liquidityTime is not null");
            return (Criteria) this;
        }

        public Criteria andLiquidityTimeEqualTo(Long value) {
            addCriterion("liquidityTime =", value, "liquidityTime");
            return (Criteria) this;
        }

        public Criteria andLiquidityTimeNotEqualTo(Long value) {
            addCriterion("liquidityTime <>", value, "liquidityTime");
            return (Criteria) this;
        }

        public Criteria andLiquidityTimeGreaterThan(Long value) {
            addCriterion("liquidityTime >", value, "liquidityTime");
            return (Criteria) this;
        }

        public Criteria andLiquidityTimeGreaterThanOrEqualTo(Long value) {
            addCriterion("liquidityTime >=", value, "liquidityTime");
            return (Criteria) this;
        }

        public Criteria andLiquidityTimeLessThan(Long value) {
            addCriterion("liquidityTime <", value, "liquidityTime");
            return (Criteria) this;
        }

        public Criteria andLiquidityTimeLessThanOrEqualTo(Long value) {
            addCriterion("liquidityTime <=", value, "liquidityTime");
            return (Criteria) this;
        }

        public Criteria andLiquidityTimeIn(List<Long> values) {
            addCriterion("liquidityTime in", values, "liquidityTime");
            return (Criteria) this;
        }

        public Criteria andLiquidityTimeNotIn(List<Long> values) {
            addCriterion("liquidityTime not in", values, "liquidityTime");
            return (Criteria) this;
        }

        public Criteria andLiquidityTimeBetween(Long value1, Long value2) {
            addCriterion("liquidityTime between", value1, value2, "liquidityTime");
            return (Criteria) this;
        }

        public Criteria andLiquidityTimeNotBetween(Long value1, Long value2) {
            addCriterion("liquidityTime not between", value1, value2, "liquidityTime");
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