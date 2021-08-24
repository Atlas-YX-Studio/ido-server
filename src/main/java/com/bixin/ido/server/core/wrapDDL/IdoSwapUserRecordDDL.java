package com.bixin.ido.server.core.wrapDDL;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class IdoSwapUserRecordDDL {
    protected String orderByClause;

    protected boolean distinct;

    protected List<Criteria> oredCriteria;

    public IdoSwapUserRecordDDL() {
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

        public Criteria andTokenInXIsNull() {
            addCriterion("tokenInX is null");
            return (Criteria) this;
        }

        public Criteria andTokenInXIsNotNull() {
            addCriterion("tokenInX is not null");
            return (Criteria) this;
        }

        public Criteria andTokenInXEqualTo(BigDecimal value) {
            addCriterion("tokenInX =", value, "tokenInX");
            return (Criteria) this;
        }

        public Criteria andTokenInXNotEqualTo(BigDecimal value) {
            addCriterion("tokenInX <>", value, "tokenInX");
            return (Criteria) this;
        }

        public Criteria andTokenInXGreaterThan(BigDecimal value) {
            addCriterion("tokenInX >", value, "tokenInX");
            return (Criteria) this;
        }

        public Criteria andTokenInXGreaterThanOrEqualTo(BigDecimal value) {
            addCriterion("tokenInX >=", value, "tokenInX");
            return (Criteria) this;
        }

        public Criteria andTokenInXLessThan(BigDecimal value) {
            addCriterion("tokenInX <", value, "tokenInX");
            return (Criteria) this;
        }

        public Criteria andTokenInXLessThanOrEqualTo(BigDecimal value) {
            addCriterion("tokenInX <=", value, "tokenInX");
            return (Criteria) this;
        }

        public Criteria andTokenInXIn(List<BigDecimal> values) {
            addCriterion("tokenInX in", values, "tokenInX");
            return (Criteria) this;
        }

        public Criteria andTokenInXNotIn(List<BigDecimal> values) {
            addCriterion("tokenInX not in", values, "tokenInX");
            return (Criteria) this;
        }

        public Criteria andTokenInXBetween(BigDecimal value1, BigDecimal value2) {
            addCriterion("tokenInX between", value1, value2, "tokenInX");
            return (Criteria) this;
        }

        public Criteria andTokenInXNotBetween(BigDecimal value1, BigDecimal value2) {
            addCriterion("tokenInX not between", value1, value2, "tokenInX");
            return (Criteria) this;
        }

        public Criteria andTokenInYIsNull() {
            addCriterion("tokenInY is null");
            return (Criteria) this;
        }

        public Criteria andTokenInYIsNotNull() {
            addCriterion("tokenInY is not null");
            return (Criteria) this;
        }

        public Criteria andTokenInYEqualTo(BigDecimal value) {
            addCriterion("tokenInY =", value, "tokenInY");
            return (Criteria) this;
        }

        public Criteria andTokenInYNotEqualTo(BigDecimal value) {
            addCriterion("tokenInY <>", value, "tokenInY");
            return (Criteria) this;
        }

        public Criteria andTokenInYGreaterThan(BigDecimal value) {
            addCriterion("tokenInY >", value, "tokenInY");
            return (Criteria) this;
        }

        public Criteria andTokenInYGreaterThanOrEqualTo(BigDecimal value) {
            addCriterion("tokenInY >=", value, "tokenInY");
            return (Criteria) this;
        }

        public Criteria andTokenInYLessThan(BigDecimal value) {
            addCriterion("tokenInY <", value, "tokenInY");
            return (Criteria) this;
        }

        public Criteria andTokenInYLessThanOrEqualTo(BigDecimal value) {
            addCriterion("tokenInY <=", value, "tokenInY");
            return (Criteria) this;
        }

        public Criteria andTokenInYIn(List<BigDecimal> values) {
            addCriterion("tokenInY in", values, "tokenInY");
            return (Criteria) this;
        }

        public Criteria andTokenInYNotIn(List<BigDecimal> values) {
            addCriterion("tokenInY not in", values, "tokenInY");
            return (Criteria) this;
        }

        public Criteria andTokenInYBetween(BigDecimal value1, BigDecimal value2) {
            addCriterion("tokenInY between", value1, value2, "tokenInY");
            return (Criteria) this;
        }

        public Criteria andTokenInYNotBetween(BigDecimal value1, BigDecimal value2) {
            addCriterion("tokenInY not between", value1, value2, "tokenInY");
            return (Criteria) this;
        }

        public Criteria andTokenOutXIsNull() {
            addCriterion("tokenOutX is null");
            return (Criteria) this;
        }

        public Criteria andTokenOutXIsNotNull() {
            addCriterion("tokenOutX is not null");
            return (Criteria) this;
        }

        public Criteria andTokenOutXEqualTo(BigDecimal value) {
            addCriterion("tokenOutX =", value, "tokenOutX");
            return (Criteria) this;
        }

        public Criteria andTokenOutXNotEqualTo(BigDecimal value) {
            addCriterion("tokenOutX <>", value, "tokenOutX");
            return (Criteria) this;
        }

        public Criteria andTokenOutXGreaterThan(BigDecimal value) {
            addCriterion("tokenOutX >", value, "tokenOutX");
            return (Criteria) this;
        }

        public Criteria andTokenOutXGreaterThanOrEqualTo(BigDecimal value) {
            addCriterion("tokenOutX >=", value, "tokenOutX");
            return (Criteria) this;
        }

        public Criteria andTokenOutXLessThan(BigDecimal value) {
            addCriterion("tokenOutX <", value, "tokenOutX");
            return (Criteria) this;
        }

        public Criteria andTokenOutXLessThanOrEqualTo(BigDecimal value) {
            addCriterion("tokenOutX <=", value, "tokenOutX");
            return (Criteria) this;
        }

        public Criteria andTokenOutXIn(List<BigDecimal> values) {
            addCriterion("tokenOutX in", values, "tokenOutX");
            return (Criteria) this;
        }

        public Criteria andTokenOutXNotIn(List<BigDecimal> values) {
            addCriterion("tokenOutX not in", values, "tokenOutX");
            return (Criteria) this;
        }

        public Criteria andTokenOutXBetween(BigDecimal value1, BigDecimal value2) {
            addCriterion("tokenOutX between", value1, value2, "tokenOutX");
            return (Criteria) this;
        }

        public Criteria andTokenOutXNotBetween(BigDecimal value1, BigDecimal value2) {
            addCriterion("tokenOutX not between", value1, value2, "tokenOutX");
            return (Criteria) this;
        }

        public Criteria andTokenOutYIsNull() {
            addCriterion("tokenOutY is null");
            return (Criteria) this;
        }

        public Criteria andTokenOutYIsNotNull() {
            addCriterion("tokenOutY is not null");
            return (Criteria) this;
        }

        public Criteria andTokenOutYEqualTo(BigDecimal value) {
            addCriterion("tokenOutY =", value, "tokenOutY");
            return (Criteria) this;
        }

        public Criteria andTokenOutYNotEqualTo(BigDecimal value) {
            addCriterion("tokenOutY <>", value, "tokenOutY");
            return (Criteria) this;
        }

        public Criteria andTokenOutYGreaterThan(BigDecimal value) {
            addCriterion("tokenOutY >", value, "tokenOutY");
            return (Criteria) this;
        }

        public Criteria andTokenOutYGreaterThanOrEqualTo(BigDecimal value) {
            addCriterion("tokenOutY >=", value, "tokenOutY");
            return (Criteria) this;
        }

        public Criteria andTokenOutYLessThan(BigDecimal value) {
            addCriterion("tokenOutY <", value, "tokenOutY");
            return (Criteria) this;
        }

        public Criteria andTokenOutYLessThanOrEqualTo(BigDecimal value) {
            addCriterion("tokenOutY <=", value, "tokenOutY");
            return (Criteria) this;
        }

        public Criteria andTokenOutYIn(List<BigDecimal> values) {
            addCriterion("tokenOutY in", values, "tokenOutY");
            return (Criteria) this;
        }

        public Criteria andTokenOutYNotIn(List<BigDecimal> values) {
            addCriterion("tokenOutY not in", values, "tokenOutY");
            return (Criteria) this;
        }

        public Criteria andTokenOutYBetween(BigDecimal value1, BigDecimal value2) {
            addCriterion("tokenOutY between", value1, value2, "tokenOutY");
            return (Criteria) this;
        }

        public Criteria andTokenOutYNotBetween(BigDecimal value1, BigDecimal value2) {
            addCriterion("tokenOutY not between", value1, value2, "tokenOutY");
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

        public Criteria andSwapTimeIsNull() {
            addCriterion("swapTime is null");
            return (Criteria) this;
        }

        public Criteria andSwapTimeIsNotNull() {
            addCriterion("swapTime is not null");
            return (Criteria) this;
        }

        public Criteria andSwapTimeEqualTo(Long value) {
            addCriterion("swapTime =", value, "swapTime");
            return (Criteria) this;
        }

        public Criteria andSwapTimeNotEqualTo(Long value) {
            addCriterion("swapTime <>", value, "swapTime");
            return (Criteria) this;
        }

        public Criteria andSwapTimeGreaterThan(Long value) {
            addCriterion("swapTime >", value, "swapTime");
            return (Criteria) this;
        }

        public Criteria andSwapTimeGreaterThanOrEqualTo(Long value) {
            addCriterion("swapTime >=", value, "swapTime");
            return (Criteria) this;
        }

        public Criteria andSwapTimeLessThan(Long value) {
            addCriterion("swapTime <", value, "swapTime");
            return (Criteria) this;
        }

        public Criteria andSwapTimeLessThanOrEqualTo(Long value) {
            addCriterion("swapTime <=", value, "swapTime");
            return (Criteria) this;
        }

        public Criteria andSwapTimeIn(List<Long> values) {
            addCriterion("swapTime in", values, "swapTime");
            return (Criteria) this;
        }

        public Criteria andSwapTimeNotIn(List<Long> values) {
            addCriterion("swapTime not in", values, "swapTime");
            return (Criteria) this;
        }

        public Criteria andSwapTimeBetween(Long value1, Long value2) {
            addCriterion("swapTime between", value1, value2, "swapTime");
            return (Criteria) this;
        }

        public Criteria andSwapTimeNotBetween(Long value1, Long value2) {
            addCriterion("swapTime not between", value1, value2, "swapTime");
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