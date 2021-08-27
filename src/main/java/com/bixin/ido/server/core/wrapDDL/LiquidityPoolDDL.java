package com.bixin.ido.server.core.wrapDDL;

import java.util.ArrayList;
import java.util.List;

public class LiquidityPoolDDL {
    protected String orderByClause;

    protected boolean distinct;

    protected List<Criteria> oredCriteria;

    public LiquidityPoolDDL() {
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