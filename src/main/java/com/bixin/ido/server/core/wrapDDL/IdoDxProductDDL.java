package com.bixin.ido.server.core.wrapDDL;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class IdoDxProductDDL {
    protected String orderByClause;

    protected boolean distinct;

    protected List<Criteria> oredCriteria;

    public IdoDxProductDDL() {
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

        public Criteria andPrdNameIsNull() {
            addCriterion("prdName is null");
            return (Criteria) this;
        }

        public Criteria andPrdNameIsNotNull() {
            addCriterion("prdName is not null");
            return (Criteria) this;
        }

        public Criteria andPrdNameEqualTo(String value) {
            addCriterion("prdName =", value, "prdName");
            return (Criteria) this;
        }

        public Criteria andPrdNameNotEqualTo(String value) {
            addCriterion("prdName <>", value, "prdName");
            return (Criteria) this;
        }

        public Criteria andPrdNameGreaterThan(String value) {
            addCriterion("prdName >", value, "prdName");
            return (Criteria) this;
        }

        public Criteria andPrdNameGreaterThanOrEqualTo(String value) {
            addCriterion("prdName >=", value, "prdName");
            return (Criteria) this;
        }

        public Criteria andPrdNameLessThan(String value) {
            addCriterion("prdName <", value, "prdName");
            return (Criteria) this;
        }

        public Criteria andPrdNameLessThanOrEqualTo(String value) {
            addCriterion("prdName <=", value, "prdName");
            return (Criteria) this;
        }

        public Criteria andPrdNameLike(String value) {
            addCriterion("prdName like", value, "prdName");
            return (Criteria) this;
        }

        public Criteria andPrdNameNotLike(String value) {
            addCriterion("prdName not like", value, "prdName");
            return (Criteria) this;
        }

        public Criteria andPrdNameIn(List<String> values) {
            addCriterion("prdName in", values, "prdName");
            return (Criteria) this;
        }

        public Criteria andPrdNameNotIn(List<String> values) {
            addCriterion("prdName not in", values, "prdName");
            return (Criteria) this;
        }

        public Criteria andPrdNameBetween(String value1, String value2) {
            addCriterion("prdName between", value1, value2, "prdName");
            return (Criteria) this;
        }

        public Criteria andPrdNameNotBetween(String value1, String value2) {
            addCriterion("prdName not between", value1, value2, "prdName");
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

        public Criteria andBaseCurrencyIsNull() {
            addCriterion("baseCurrency is null");
            return (Criteria) this;
        }

        public Criteria andBaseCurrencyIsNotNull() {
            addCriterion("baseCurrency is not null");
            return (Criteria) this;
        }

        public Criteria andBaseCurrencyEqualTo(String value) {
            addCriterion("baseCurrency =", value, "baseCurrency");
            return (Criteria) this;
        }

        public Criteria andBaseCurrencyNotEqualTo(String value) {
            addCriterion("baseCurrency <>", value, "baseCurrency");
            return (Criteria) this;
        }

        public Criteria andBaseCurrencyGreaterThan(String value) {
            addCriterion("baseCurrency >", value, "baseCurrency");
            return (Criteria) this;
        }

        public Criteria andBaseCurrencyGreaterThanOrEqualTo(String value) {
            addCriterion("baseCurrency >=", value, "baseCurrency");
            return (Criteria) this;
        }

        public Criteria andBaseCurrencyLessThan(String value) {
            addCriterion("baseCurrency <", value, "baseCurrency");
            return (Criteria) this;
        }

        public Criteria andBaseCurrencyLessThanOrEqualTo(String value) {
            addCriterion("baseCurrency <=", value, "baseCurrency");
            return (Criteria) this;
        }

        public Criteria andBaseCurrencyLike(String value) {
            addCriterion("baseCurrency like", value, "baseCurrency");
            return (Criteria) this;
        }

        public Criteria andBaseCurrencyNotLike(String value) {
            addCriterion("baseCurrency not like", value, "baseCurrency");
            return (Criteria) this;
        }

        public Criteria andBaseCurrencyIn(List<String> values) {
            addCriterion("baseCurrency in", values, "baseCurrency");
            return (Criteria) this;
        }

        public Criteria andBaseCurrencyNotIn(List<String> values) {
            addCriterion("baseCurrency not in", values, "baseCurrency");
            return (Criteria) this;
        }

        public Criteria andBaseCurrencyBetween(String value1, String value2) {
            addCriterion("baseCurrency between", value1, value2, "baseCurrency");
            return (Criteria) this;
        }

        public Criteria andBaseCurrencyNotBetween(String value1, String value2) {
            addCriterion("baseCurrency not between", value1, value2, "baseCurrency");
            return (Criteria) this;
        }

        public Criteria andRateIsNull() {
            addCriterion("rate is null");
            return (Criteria) this;
        }

        public Criteria andRateIsNotNull() {
            addCriterion("rate is not null");
            return (Criteria) this;
        }

        public Criteria andRateEqualTo(BigDecimal value) {
            addCriterion("rate =", value, "rate");
            return (Criteria) this;
        }

        public Criteria andRateNotEqualTo(BigDecimal value) {
            addCriterion("rate <>", value, "rate");
            return (Criteria) this;
        }

        public Criteria andRateGreaterThan(BigDecimal value) {
            addCriterion("rate >", value, "rate");
            return (Criteria) this;
        }

        public Criteria andRateGreaterThanOrEqualTo(BigDecimal value) {
            addCriterion("rate >=", value, "rate");
            return (Criteria) this;
        }

        public Criteria andRateLessThan(BigDecimal value) {
            addCriterion("rate <", value, "rate");
            return (Criteria) this;
        }

        public Criteria andRateLessThanOrEqualTo(BigDecimal value) {
            addCriterion("rate <=", value, "rate");
            return (Criteria) this;
        }

        public Criteria andRateIn(List<BigDecimal> values) {
            addCriterion("rate in", values, "rate");
            return (Criteria) this;
        }

        public Criteria andRateNotIn(List<BigDecimal> values) {
            addCriterion("rate not in", values, "rate");
            return (Criteria) this;
        }

        public Criteria andRateBetween(BigDecimal value1, BigDecimal value2) {
            addCriterion("rate between", value1, value2, "rate");
            return (Criteria) this;
        }

        public Criteria andRateNotBetween(BigDecimal value1, BigDecimal value2) {
            addCriterion("rate not between", value1, value2, "rate");
            return (Criteria) this;
        }

        public Criteria andRaiseTotalIsNull() {
            addCriterion("raiseTotal is null");
            return (Criteria) this;
        }

        public Criteria andRaiseTotalIsNotNull() {
            addCriterion("raiseTotal is not null");
            return (Criteria) this;
        }

        public Criteria andRaiseTotalEqualTo(BigDecimal value) {
            addCriterion("raiseTotal =", value, "raiseTotal");
            return (Criteria) this;
        }

        public Criteria andRaiseTotalNotEqualTo(BigDecimal value) {
            addCriterion("raiseTotal <>", value, "raiseTotal");
            return (Criteria) this;
        }

        public Criteria andRaiseTotalGreaterThan(BigDecimal value) {
            addCriterion("raiseTotal >", value, "raiseTotal");
            return (Criteria) this;
        }

        public Criteria andRaiseTotalGreaterThanOrEqualTo(BigDecimal value) {
            addCriterion("raiseTotal >=", value, "raiseTotal");
            return (Criteria) this;
        }

        public Criteria andRaiseTotalLessThan(BigDecimal value) {
            addCriterion("raiseTotal <", value, "raiseTotal");
            return (Criteria) this;
        }

        public Criteria andRaiseTotalLessThanOrEqualTo(BigDecimal value) {
            addCriterion("raiseTotal <=", value, "raiseTotal");
            return (Criteria) this;
        }

        public Criteria andRaiseTotalIn(List<BigDecimal> values) {
            addCriterion("raiseTotal in", values, "raiseTotal");
            return (Criteria) this;
        }

        public Criteria andRaiseTotalNotIn(List<BigDecimal> values) {
            addCriterion("raiseTotal not in", values, "raiseTotal");
            return (Criteria) this;
        }

        public Criteria andRaiseTotalBetween(BigDecimal value1, BigDecimal value2) {
            addCriterion("raiseTotal between", value1, value2, "raiseTotal");
            return (Criteria) this;
        }

        public Criteria andRaiseTotalNotBetween(BigDecimal value1, BigDecimal value2) {
            addCriterion("raiseTotal not between", value1, value2, "raiseTotal");
            return (Criteria) this;
        }

        public Criteria andCurrencyTotalIsNull() {
            addCriterion("currencyTotal is null");
            return (Criteria) this;
        }

        public Criteria andCurrencyTotalIsNotNull() {
            addCriterion("currencyTotal is not null");
            return (Criteria) this;
        }

        public Criteria andCurrencyTotalEqualTo(BigDecimal value) {
            addCriterion("currencyTotal =", value, "currencyTotal");
            return (Criteria) this;
        }

        public Criteria andCurrencyTotalNotEqualTo(BigDecimal value) {
            addCriterion("currencyTotal <>", value, "currencyTotal");
            return (Criteria) this;
        }

        public Criteria andCurrencyTotalGreaterThan(BigDecimal value) {
            addCriterion("currencyTotal >", value, "currencyTotal");
            return (Criteria) this;
        }

        public Criteria andCurrencyTotalGreaterThanOrEqualTo(BigDecimal value) {
            addCriterion("currencyTotal >=", value, "currencyTotal");
            return (Criteria) this;
        }

        public Criteria andCurrencyTotalLessThan(BigDecimal value) {
            addCriterion("currencyTotal <", value, "currencyTotal");
            return (Criteria) this;
        }

        public Criteria andCurrencyTotalLessThanOrEqualTo(BigDecimal value) {
            addCriterion("currencyTotal <=", value, "currencyTotal");
            return (Criteria) this;
        }

        public Criteria andCurrencyTotalIn(List<BigDecimal> values) {
            addCriterion("currencyTotal in", values, "currencyTotal");
            return (Criteria) this;
        }

        public Criteria andCurrencyTotalNotIn(List<BigDecimal> values) {
            addCriterion("currencyTotal not in", values, "currencyTotal");
            return (Criteria) this;
        }

        public Criteria andCurrencyTotalBetween(BigDecimal value1, BigDecimal value2) {
            addCriterion("currencyTotal between", value1, value2, "currencyTotal");
            return (Criteria) this;
        }

        public Criteria andCurrencyTotalNotBetween(BigDecimal value1, BigDecimal value2) {
            addCriterion("currencyTotal not between", value1, value2, "currencyTotal");
            return (Criteria) this;
        }

        public Criteria andTokenPrecisionIsNull() {
            addCriterion("tokenPrecision is null");
            return (Criteria) this;
        }

        public Criteria andTokenPrecisionIsNotNull() {
            addCriterion("tokenPrecision is not null");
            return (Criteria) this;
        }

        public Criteria andTokenPrecisionEqualTo(Short value) {
            addCriterion("tokenPrecision =", value, "tokenPrecision");
            return (Criteria) this;
        }

        public Criteria andTokenPrecisionNotEqualTo(Short value) {
            addCriterion("tokenPrecision <>", value, "tokenPrecision");
            return (Criteria) this;
        }

        public Criteria andTokenPrecisionGreaterThan(Short value) {
            addCriterion("tokenPrecision >", value, "tokenPrecision");
            return (Criteria) this;
        }

        public Criteria andTokenPrecisionGreaterThanOrEqualTo(Short value) {
            addCriterion("tokenPrecision >=", value, "tokenPrecision");
            return (Criteria) this;
        }

        public Criteria andTokenPrecisionLessThan(Short value) {
            addCriterion("tokenPrecision <", value, "tokenPrecision");
            return (Criteria) this;
        }

        public Criteria andTokenPrecisionLessThanOrEqualTo(Short value) {
            addCriterion("tokenPrecision <=", value, "tokenPrecision");
            return (Criteria) this;
        }

        public Criteria andTokenPrecisionIn(List<Short> values) {
            addCriterion("tokenPrecision in", values, "tokenPrecision");
            return (Criteria) this;
        }

        public Criteria andTokenPrecisionNotIn(List<Short> values) {
            addCriterion("tokenPrecision not in", values, "tokenPrecision");
            return (Criteria) this;
        }

        public Criteria andTokenPrecisionBetween(Short value1, Short value2) {
            addCriterion("tokenPrecision between", value1, value2, "tokenPrecision");
            return (Criteria) this;
        }

        public Criteria andTokenPrecisionNotBetween(Short value1, Short value2) {
            addCriterion("tokenPrecision not between", value1, value2, "tokenPrecision");
            return (Criteria) this;
        }

        public Criteria andAddressIsNull() {
            addCriterion("address is null");
            return (Criteria) this;
        }

        public Criteria andAddressIsNotNull() {
            addCriterion("address is not null");
            return (Criteria) this;
        }

        public Criteria andAddressEqualTo(String value) {
            addCriterion("address =", value, "address");
            return (Criteria) this;
        }

        public Criteria andAddressNotEqualTo(String value) {
            addCriterion("address <>", value, "address");
            return (Criteria) this;
        }

        public Criteria andAddressGreaterThan(String value) {
            addCriterion("address >", value, "address");
            return (Criteria) this;
        }

        public Criteria andAddressGreaterThanOrEqualTo(String value) {
            addCriterion("address >=", value, "address");
            return (Criteria) this;
        }

        public Criteria andAddressLessThan(String value) {
            addCriterion("address <", value, "address");
            return (Criteria) this;
        }

        public Criteria andAddressLessThanOrEqualTo(String value) {
            addCriterion("address <=", value, "address");
            return (Criteria) this;
        }

        public Criteria andAddressLike(String value) {
            addCriterion("address like", value, "address");
            return (Criteria) this;
        }

        public Criteria andAddressNotLike(String value) {
            addCriterion("address not like", value, "address");
            return (Criteria) this;
        }

        public Criteria andAddressIn(List<String> values) {
            addCriterion("address in", values, "address");
            return (Criteria) this;
        }

        public Criteria andAddressNotIn(List<String> values) {
            addCriterion("address not in", values, "address");
            return (Criteria) this;
        }

        public Criteria andAddressBetween(String value1, String value2) {
            addCriterion("address between", value1, value2, "address");
            return (Criteria) this;
        }

        public Criteria andAddressNotBetween(String value1, String value2) {
            addCriterion("address not between", value1, value2, "address");
            return (Criteria) this;
        }

        public Criteria andIconIsNull() {
            addCriterion("icon is null");
            return (Criteria) this;
        }

        public Criteria andIconIsNotNull() {
            addCriterion("icon is not null");
            return (Criteria) this;
        }

        public Criteria andIconEqualTo(String value) {
            addCriterion("icon =", value, "icon");
            return (Criteria) this;
        }

        public Criteria andIconNotEqualTo(String value) {
            addCriterion("icon <>", value, "icon");
            return (Criteria) this;
        }

        public Criteria andIconGreaterThan(String value) {
            addCriterion("icon >", value, "icon");
            return (Criteria) this;
        }

        public Criteria andIconGreaterThanOrEqualTo(String value) {
            addCriterion("icon >=", value, "icon");
            return (Criteria) this;
        }

        public Criteria andIconLessThan(String value) {
            addCriterion("icon <", value, "icon");
            return (Criteria) this;
        }

        public Criteria andIconLessThanOrEqualTo(String value) {
            addCriterion("icon <=", value, "icon");
            return (Criteria) this;
        }

        public Criteria andIconLike(String value) {
            addCriterion("icon like", value, "icon");
            return (Criteria) this;
        }

        public Criteria andIconNotLike(String value) {
            addCriterion("icon not like", value, "icon");
            return (Criteria) this;
        }

        public Criteria andIconIn(List<String> values) {
            addCriterion("icon in", values, "icon");
            return (Criteria) this;
        }

        public Criteria andIconNotIn(List<String> values) {
            addCriterion("icon not in", values, "icon");
            return (Criteria) this;
        }

        public Criteria andIconBetween(String value1, String value2) {
            addCriterion("icon between", value1, value2, "icon");
            return (Criteria) this;
        }

        public Criteria andIconNotBetween(String value1, String value2) {
            addCriterion("icon not between", value1, value2, "icon");
            return (Criteria) this;
        }

        public Criteria andStateIsNull() {
            addCriterion("state is null");
            return (Criteria) this;
        }

        public Criteria andStateIsNotNull() {
            addCriterion("state is not null");
            return (Criteria) this;
        }

        public Criteria andStateEqualTo(String value) {
            addCriterion("state =", value, "state");
            return (Criteria) this;
        }

        public Criteria andStateNotEqualTo(String value) {
            addCriterion("state <>", value, "state");
            return (Criteria) this;
        }

        public Criteria andStateGreaterThan(String value) {
            addCriterion("state >", value, "state");
            return (Criteria) this;
        }

        public Criteria andStateGreaterThanOrEqualTo(String value) {
            addCriterion("state >=", value, "state");
            return (Criteria) this;
        }

        public Criteria andStateLessThan(String value) {
            addCriterion("state <", value, "state");
            return (Criteria) this;
        }

        public Criteria andStateLessThanOrEqualTo(String value) {
            addCriterion("state <=", value, "state");
            return (Criteria) this;
        }

        public Criteria andStateLike(String value) {
            addCriterion("state like", value, "state");
            return (Criteria) this;
        }

        public Criteria andStateNotLike(String value) {
            addCriterion("state not like", value, "state");
            return (Criteria) this;
        }

        public Criteria andStateIn(List<String> values) {
            addCriterion("state in", values, "state");
            return (Criteria) this;
        }

        public Criteria andStateNotIn(List<String> values) {
            addCriterion("state not in", values, "state");
            return (Criteria) this;
        }

        public Criteria andStateBetween(String value1, String value2) {
            addCriterion("state between", value1, value2, "state");
            return (Criteria) this;
        }

        public Criteria andStateNotBetween(String value1, String value2) {
            addCriterion("state not between", value1, value2, "state");
            return (Criteria) this;
        }

        public Criteria andPrdDescIsNull() {
            addCriterion("prdDesc is null");
            return (Criteria) this;
        }

        public Criteria andPrdDescIsNotNull() {
            addCriterion("prdDesc is not null");
            return (Criteria) this;
        }

        public Criteria andPrdDescEqualTo(String value) {
            addCriterion("prdDesc =", value, "prdDesc");
            return (Criteria) this;
        }

        public Criteria andPrdDescNotEqualTo(String value) {
            addCriterion("prdDesc <>", value, "prdDesc");
            return (Criteria) this;
        }

        public Criteria andPrdDescGreaterThan(String value) {
            addCriterion("prdDesc >", value, "prdDesc");
            return (Criteria) this;
        }

        public Criteria andPrdDescGreaterThanOrEqualTo(String value) {
            addCriterion("prdDesc >=", value, "prdDesc");
            return (Criteria) this;
        }

        public Criteria andPrdDescLessThan(String value) {
            addCriterion("prdDesc <", value, "prdDesc");
            return (Criteria) this;
        }

        public Criteria andPrdDescLessThanOrEqualTo(String value) {
            addCriterion("prdDesc <=", value, "prdDesc");
            return (Criteria) this;
        }

        public Criteria andPrdDescLike(String value) {
            addCriterion("prdDesc like", value, "prdDesc");
            return (Criteria) this;
        }

        public Criteria andPrdDescNotLike(String value) {
            addCriterion("prdDesc not like", value, "prdDesc");
            return (Criteria) this;
        }

        public Criteria andPrdDescIn(List<String> values) {
            addCriterion("prdDesc in", values, "prdDesc");
            return (Criteria) this;
        }

        public Criteria andPrdDescNotIn(List<String> values) {
            addCriterion("prdDesc not in", values, "prdDesc");
            return (Criteria) this;
        }

        public Criteria andPrdDescBetween(String value1, String value2) {
            addCriterion("prdDesc between", value1, value2, "prdDesc");
            return (Criteria) this;
        }

        public Criteria andPrdDescNotBetween(String value1, String value2) {
            addCriterion("prdDesc not between", value1, value2, "prdDesc");
            return (Criteria) this;
        }

        public Criteria andPrdDescEnIsNull() {
            addCriterion("prdDescEn is null");
            return (Criteria) this;
        }

        public Criteria andPrdDescEnIsNotNull() {
            addCriterion("prdDescEn is not null");
            return (Criteria) this;
        }

        public Criteria andPrdDescEnEqualTo(String value) {
            addCriterion("prdDescEn =", value, "prdDescEn");
            return (Criteria) this;
        }

        public Criteria andPrdDescEnNotEqualTo(String value) {
            addCriterion("prdDescEn <>", value, "prdDescEn");
            return (Criteria) this;
        }

        public Criteria andPrdDescEnGreaterThan(String value) {
            addCriterion("prdDescEn >", value, "prdDescEn");
            return (Criteria) this;
        }

        public Criteria andPrdDescEnGreaterThanOrEqualTo(String value) {
            addCriterion("prdDescEn >=", value, "prdDescEn");
            return (Criteria) this;
        }

        public Criteria andPrdDescEnLessThan(String value) {
            addCriterion("prdDescEn <", value, "prdDescEn");
            return (Criteria) this;
        }

        public Criteria andPrdDescEnLessThanOrEqualTo(String value) {
            addCriterion("prdDescEn <=", value, "prdDescEn");
            return (Criteria) this;
        }

        public Criteria andPrdDescEnLike(String value) {
            addCriterion("prdDescEn like", value, "prdDescEn");
            return (Criteria) this;
        }

        public Criteria andPrdDescEnNotLike(String value) {
            addCriterion("prdDescEn not like", value, "prdDescEn");
            return (Criteria) this;
        }

        public Criteria andPrdDescEnIn(List<String> values) {
            addCriterion("prdDescEn in", values, "prdDescEn");
            return (Criteria) this;
        }

        public Criteria andPrdDescEnNotIn(List<String> values) {
            addCriterion("prdDescEn not in", values, "prdDescEn");
            return (Criteria) this;
        }

        public Criteria andPrdDescEnBetween(String value1, String value2) {
            addCriterion("prdDescEn between", value1, value2, "prdDescEn");
            return (Criteria) this;
        }

        public Criteria andPrdDescEnNotBetween(String value1, String value2) {
            addCriterion("prdDescEn not between", value1, value2, "prdDescEn");
            return (Criteria) this;
        }

        public Criteria andRuleDescIsNull() {
            addCriterion("ruleDesc is null");
            return (Criteria) this;
        }

        public Criteria andRuleDescIsNotNull() {
            addCriterion("ruleDesc is not null");
            return (Criteria) this;
        }

        public Criteria andRuleDescEqualTo(String value) {
            addCriterion("ruleDesc =", value, "ruleDesc");
            return (Criteria) this;
        }

        public Criteria andRuleDescNotEqualTo(String value) {
            addCriterion("ruleDesc <>", value, "ruleDesc");
            return (Criteria) this;
        }

        public Criteria andRuleDescGreaterThan(String value) {
            addCriterion("ruleDesc >", value, "ruleDesc");
            return (Criteria) this;
        }

        public Criteria andRuleDescGreaterThanOrEqualTo(String value) {
            addCriterion("ruleDesc >=", value, "ruleDesc");
            return (Criteria) this;
        }

        public Criteria andRuleDescLessThan(String value) {
            addCriterion("ruleDesc <", value, "ruleDesc");
            return (Criteria) this;
        }

        public Criteria andRuleDescLessThanOrEqualTo(String value) {
            addCriterion("ruleDesc <=", value, "ruleDesc");
            return (Criteria) this;
        }

        public Criteria andRuleDescLike(String value) {
            addCriterion("ruleDesc like", value, "ruleDesc");
            return (Criteria) this;
        }

        public Criteria andRuleDescNotLike(String value) {
            addCriterion("ruleDesc not like", value, "ruleDesc");
            return (Criteria) this;
        }

        public Criteria andRuleDescIn(List<String> values) {
            addCriterion("ruleDesc in", values, "ruleDesc");
            return (Criteria) this;
        }

        public Criteria andRuleDescNotIn(List<String> values) {
            addCriterion("ruleDesc not in", values, "ruleDesc");
            return (Criteria) this;
        }

        public Criteria andRuleDescBetween(String value1, String value2) {
            addCriterion("ruleDesc between", value1, value2, "ruleDesc");
            return (Criteria) this;
        }

        public Criteria andRuleDescNotBetween(String value1, String value2) {
            addCriterion("ruleDesc not between", value1, value2, "ruleDesc");
            return (Criteria) this;
        }

        public Criteria andRuleDescEnIsNull() {
            addCriterion("ruleDescEn is null");
            return (Criteria) this;
        }

        public Criteria andRuleDescEnIsNotNull() {
            addCriterion("ruleDescEn is not null");
            return (Criteria) this;
        }

        public Criteria andRuleDescEnEqualTo(String value) {
            addCriterion("ruleDescEn =", value, "ruleDescEn");
            return (Criteria) this;
        }

        public Criteria andRuleDescEnNotEqualTo(String value) {
            addCriterion("ruleDescEn <>", value, "ruleDescEn");
            return (Criteria) this;
        }

        public Criteria andRuleDescEnGreaterThan(String value) {
            addCriterion("ruleDescEn >", value, "ruleDescEn");
            return (Criteria) this;
        }

        public Criteria andRuleDescEnGreaterThanOrEqualTo(String value) {
            addCriterion("ruleDescEn >=", value, "ruleDescEn");
            return (Criteria) this;
        }

        public Criteria andRuleDescEnLessThan(String value) {
            addCriterion("ruleDescEn <", value, "ruleDescEn");
            return (Criteria) this;
        }

        public Criteria andRuleDescEnLessThanOrEqualTo(String value) {
            addCriterion("ruleDescEn <=", value, "ruleDescEn");
            return (Criteria) this;
        }

        public Criteria andRuleDescEnLike(String value) {
            addCriterion("ruleDescEn like", value, "ruleDescEn");
            return (Criteria) this;
        }

        public Criteria andRuleDescEnNotLike(String value) {
            addCriterion("ruleDescEn not like", value, "ruleDescEn");
            return (Criteria) this;
        }

        public Criteria andRuleDescEnIn(List<String> values) {
            addCriterion("ruleDescEn in", values, "ruleDescEn");
            return (Criteria) this;
        }

        public Criteria andRuleDescEnNotIn(List<String> values) {
            addCriterion("ruleDescEn not in", values, "ruleDescEn");
            return (Criteria) this;
        }

        public Criteria andRuleDescEnBetween(String value1, String value2) {
            addCriterion("ruleDescEn between", value1, value2, "ruleDescEn");
            return (Criteria) this;
        }

        public Criteria andRuleDescEnNotBetween(String value1, String value2) {
            addCriterion("ruleDescEn not between", value1, value2, "ruleDescEn");
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

        public Criteria andStartTimeIsNull() {
            addCriterion("startTime is null");
            return (Criteria) this;
        }

        public Criteria andStartTimeIsNotNull() {
            addCriterion("startTime is not null");
            return (Criteria) this;
        }

        public Criteria andStartTimeEqualTo(Long value) {
            addCriterion("startTime =", value, "startTime");
            return (Criteria) this;
        }

        public Criteria andStartTimeNotEqualTo(Long value) {
            addCriterion("startTime <>", value, "startTime");
            return (Criteria) this;
        }

        public Criteria andStartTimeGreaterThan(Long value) {
            addCriterion("startTime >", value, "startTime");
            return (Criteria) this;
        }

        public Criteria andStartTimeGreaterThanOrEqualTo(Long value) {
            addCriterion("startTime >=", value, "startTime");
            return (Criteria) this;
        }

        public Criteria andStartTimeLessThan(Long value) {
            addCriterion("startTime <", value, "startTime");
            return (Criteria) this;
        }

        public Criteria andStartTimeLessThanOrEqualTo(Long value) {
            addCriterion("startTime <=", value, "startTime");
            return (Criteria) this;
        }

        public Criteria andStartTimeIn(List<Long> values) {
            addCriterion("startTime in", values, "startTime");
            return (Criteria) this;
        }

        public Criteria andStartTimeNotIn(List<Long> values) {
            addCriterion("startTime not in", values, "startTime");
            return (Criteria) this;
        }

        public Criteria andStartTimeBetween(Long value1, Long value2) {
            addCriterion("startTime between", value1, value2, "startTime");
            return (Criteria) this;
        }

        public Criteria andStartTimeNotBetween(Long value1, Long value2) {
            addCriterion("startTime not between", value1, value2, "startTime");
            return (Criteria) this;
        }

        public Criteria andEndTimeIsNull() {
            addCriterion("endTime is null");
            return (Criteria) this;
        }

        public Criteria andEndTimeIsNotNull() {
            addCriterion("endTime is not null");
            return (Criteria) this;
        }

        public Criteria andEndTimeEqualTo(Long value) {
            addCriterion("endTime =", value, "endTime");
            return (Criteria) this;
        }

        public Criteria andEndTimeNotEqualTo(Long value) {
            addCriterion("endTime <>", value, "endTime");
            return (Criteria) this;
        }

        public Criteria andEndTimeGreaterThan(Long value) {
            addCriterion("endTime >", value, "endTime");
            return (Criteria) this;
        }

        public Criteria andEndTimeGreaterThanOrEqualTo(Long value) {
            addCriterion("endTime >=", value, "endTime");
            return (Criteria) this;
        }

        public Criteria andEndTimeLessThan(Long value) {
            addCriterion("endTime <", value, "endTime");
            return (Criteria) this;
        }

        public Criteria andEndTimeLessThanOrEqualTo(Long value) {
            addCriterion("endTime <=", value, "endTime");
            return (Criteria) this;
        }

        public Criteria andEndTimeIn(List<Long> values) {
            addCriterion("endTime in", values, "endTime");
            return (Criteria) this;
        }

        public Criteria andEndTimeNotIn(List<Long> values) {
            addCriterion("endTime not in", values, "endTime");
            return (Criteria) this;
        }

        public Criteria andEndTimeBetween(Long value1, Long value2) {
            addCriterion("endTime between", value1, value2, "endTime");
            return (Criteria) this;
        }

        public Criteria andEndTimeNotBetween(Long value1, Long value2) {
            addCriterion("endTime not between", value1, value2, "endTime");
            return (Criteria) this;
        }

        public Criteria andPledgeStartTimeIsNull() {
            addCriterion("pledgeStartTime is null");
            return (Criteria) this;
        }

        public Criteria andPledgeStartTimeIsNotNull() {
            addCriterion("pledgeStartTime is not null");
            return (Criteria) this;
        }

        public Criteria andPledgeStartTimeEqualTo(Long value) {
            addCriterion("pledgeStartTime =", value, "pledgeStartTime");
            return (Criteria) this;
        }

        public Criteria andPledgeStartTimeNotEqualTo(Long value) {
            addCriterion("pledgeStartTime <>", value, "pledgeStartTime");
            return (Criteria) this;
        }

        public Criteria andPledgeStartTimeGreaterThan(Long value) {
            addCriterion("pledgeStartTime >", value, "pledgeStartTime");
            return (Criteria) this;
        }

        public Criteria andPledgeStartTimeGreaterThanOrEqualTo(Long value) {
            addCriterion("pledgeStartTime >=", value, "pledgeStartTime");
            return (Criteria) this;
        }

        public Criteria andPledgeStartTimeLessThan(Long value) {
            addCriterion("pledgeStartTime <", value, "pledgeStartTime");
            return (Criteria) this;
        }

        public Criteria andPledgeStartTimeLessThanOrEqualTo(Long value) {
            addCriterion("pledgeStartTime <=", value, "pledgeStartTime");
            return (Criteria) this;
        }

        public Criteria andPledgeStartTimeIn(List<Long> values) {
            addCriterion("pledgeStartTime in", values, "pledgeStartTime");
            return (Criteria) this;
        }

        public Criteria andPledgeStartTimeNotIn(List<Long> values) {
            addCriterion("pledgeStartTime not in", values, "pledgeStartTime");
            return (Criteria) this;
        }

        public Criteria andPledgeStartTimeBetween(Long value1, Long value2) {
            addCriterion("pledgeStartTime between", value1, value2, "pledgeStartTime");
            return (Criteria) this;
        }

        public Criteria andPledgeStartTimeNotBetween(Long value1, Long value2) {
            addCriterion("pledgeStartTime not between", value1, value2, "pledgeStartTime");
            return (Criteria) this;
        }

        public Criteria andPledgeEndTimeIsNull() {
            addCriterion("pledgeEndTime is null");
            return (Criteria) this;
        }

        public Criteria andPledgeEndTimeIsNotNull() {
            addCriterion("pledgeEndTime is not null");
            return (Criteria) this;
        }

        public Criteria andPledgeEndTimeEqualTo(Long value) {
            addCriterion("pledgeEndTime =", value, "pledgeEndTime");
            return (Criteria) this;
        }

        public Criteria andPledgeEndTimeNotEqualTo(Long value) {
            addCriterion("pledgeEndTime <>", value, "pledgeEndTime");
            return (Criteria) this;
        }

        public Criteria andPledgeEndTimeGreaterThan(Long value) {
            addCriterion("pledgeEndTime >", value, "pledgeEndTime");
            return (Criteria) this;
        }

        public Criteria andPledgeEndTimeGreaterThanOrEqualTo(Long value) {
            addCriterion("pledgeEndTime >=", value, "pledgeEndTime");
            return (Criteria) this;
        }

        public Criteria andPledgeEndTimeLessThan(Long value) {
            addCriterion("pledgeEndTime <", value, "pledgeEndTime");
            return (Criteria) this;
        }

        public Criteria andPledgeEndTimeLessThanOrEqualTo(Long value) {
            addCriterion("pledgeEndTime <=", value, "pledgeEndTime");
            return (Criteria) this;
        }

        public Criteria andPledgeEndTimeIn(List<Long> values) {
            addCriterion("pledgeEndTime in", values, "pledgeEndTime");
            return (Criteria) this;
        }

        public Criteria andPledgeEndTimeNotIn(List<Long> values) {
            addCriterion("pledgeEndTime not in", values, "pledgeEndTime");
            return (Criteria) this;
        }

        public Criteria andPledgeEndTimeBetween(Long value1, Long value2) {
            addCriterion("pledgeEndTime between", value1, value2, "pledgeEndTime");
            return (Criteria) this;
        }

        public Criteria andPledgeEndTimeNotBetween(Long value1, Long value2) {
            addCriterion("pledgeEndTime not between", value1, value2, "pledgeEndTime");
            return (Criteria) this;
        }

        public Criteria andLockStartTimeIsNull() {
            addCriterion("lockStartTime is null");
            return (Criteria) this;
        }

        public Criteria andLockStartTimeIsNotNull() {
            addCriterion("lockStartTime is not null");
            return (Criteria) this;
        }

        public Criteria andLockStartTimeEqualTo(Long value) {
            addCriterion("lockStartTime =", value, "lockStartTime");
            return (Criteria) this;
        }

        public Criteria andLockStartTimeNotEqualTo(Long value) {
            addCriterion("lockStartTime <>", value, "lockStartTime");
            return (Criteria) this;
        }

        public Criteria andLockStartTimeGreaterThan(Long value) {
            addCriterion("lockStartTime >", value, "lockStartTime");
            return (Criteria) this;
        }

        public Criteria andLockStartTimeGreaterThanOrEqualTo(Long value) {
            addCriterion("lockStartTime >=", value, "lockStartTime");
            return (Criteria) this;
        }

        public Criteria andLockStartTimeLessThan(Long value) {
            addCriterion("lockStartTime <", value, "lockStartTime");
            return (Criteria) this;
        }

        public Criteria andLockStartTimeLessThanOrEqualTo(Long value) {
            addCriterion("lockStartTime <=", value, "lockStartTime");
            return (Criteria) this;
        }

        public Criteria andLockStartTimeIn(List<Long> values) {
            addCriterion("lockStartTime in", values, "lockStartTime");
            return (Criteria) this;
        }

        public Criteria andLockStartTimeNotIn(List<Long> values) {
            addCriterion("lockStartTime not in", values, "lockStartTime");
            return (Criteria) this;
        }

        public Criteria andLockStartTimeBetween(Long value1, Long value2) {
            addCriterion("lockStartTime between", value1, value2, "lockStartTime");
            return (Criteria) this;
        }

        public Criteria andLockStartTimeNotBetween(Long value1, Long value2) {
            addCriterion("lockStartTime not between", value1, value2, "lockStartTime");
            return (Criteria) this;
        }

        public Criteria andLockEndTimeIsNull() {
            addCriterion("lockEndTime is null");
            return (Criteria) this;
        }

        public Criteria andLockEndTimeIsNotNull() {
            addCriterion("lockEndTime is not null");
            return (Criteria) this;
        }

        public Criteria andLockEndTimeEqualTo(Long value) {
            addCriterion("lockEndTime =", value, "lockEndTime");
            return (Criteria) this;
        }

        public Criteria andLockEndTimeNotEqualTo(Long value) {
            addCriterion("lockEndTime <>", value, "lockEndTime");
            return (Criteria) this;
        }

        public Criteria andLockEndTimeGreaterThan(Long value) {
            addCriterion("lockEndTime >", value, "lockEndTime");
            return (Criteria) this;
        }

        public Criteria andLockEndTimeGreaterThanOrEqualTo(Long value) {
            addCriterion("lockEndTime >=", value, "lockEndTime");
            return (Criteria) this;
        }

        public Criteria andLockEndTimeLessThan(Long value) {
            addCriterion("lockEndTime <", value, "lockEndTime");
            return (Criteria) this;
        }

        public Criteria andLockEndTimeLessThanOrEqualTo(Long value) {
            addCriterion("lockEndTime <=", value, "lockEndTime");
            return (Criteria) this;
        }

        public Criteria andLockEndTimeIn(List<Long> values) {
            addCriterion("lockEndTime in", values, "lockEndTime");
            return (Criteria) this;
        }

        public Criteria andLockEndTimeNotIn(List<Long> values) {
            addCriterion("lockEndTime not in", values, "lockEndTime");
            return (Criteria) this;
        }

        public Criteria andLockEndTimeBetween(Long value1, Long value2) {
            addCriterion("lockEndTime between", value1, value2, "lockEndTime");
            return (Criteria) this;
        }

        public Criteria andLockEndTimeNotBetween(Long value1, Long value2) {
            addCriterion("lockEndTime not between", value1, value2, "lockEndTime");
            return (Criteria) this;
        }

        public Criteria andPayStartTimeIsNull() {
            addCriterion("payStartTime is null");
            return (Criteria) this;
        }

        public Criteria andPayStartTimeIsNotNull() {
            addCriterion("payStartTime is not null");
            return (Criteria) this;
        }

        public Criteria andPayStartTimeEqualTo(Long value) {
            addCriterion("payStartTime =", value, "payStartTime");
            return (Criteria) this;
        }

        public Criteria andPayStartTimeNotEqualTo(Long value) {
            addCriterion("payStartTime <>", value, "payStartTime");
            return (Criteria) this;
        }

        public Criteria andPayStartTimeGreaterThan(Long value) {
            addCriterion("payStartTime >", value, "payStartTime");
            return (Criteria) this;
        }

        public Criteria andPayStartTimeGreaterThanOrEqualTo(Long value) {
            addCriterion("payStartTime >=", value, "payStartTime");
            return (Criteria) this;
        }

        public Criteria andPayStartTimeLessThan(Long value) {
            addCriterion("payStartTime <", value, "payStartTime");
            return (Criteria) this;
        }

        public Criteria andPayStartTimeLessThanOrEqualTo(Long value) {
            addCriterion("payStartTime <=", value, "payStartTime");
            return (Criteria) this;
        }

        public Criteria andPayStartTimeIn(List<Long> values) {
            addCriterion("payStartTime in", values, "payStartTime");
            return (Criteria) this;
        }

        public Criteria andPayStartTimeNotIn(List<Long> values) {
            addCriterion("payStartTime not in", values, "payStartTime");
            return (Criteria) this;
        }

        public Criteria andPayStartTimeBetween(Long value1, Long value2) {
            addCriterion("payStartTime between", value1, value2, "payStartTime");
            return (Criteria) this;
        }

        public Criteria andPayStartTimeNotBetween(Long value1, Long value2) {
            addCriterion("payStartTime not between", value1, value2, "payStartTime");
            return (Criteria) this;
        }

        public Criteria andPayEndTimeIsNull() {
            addCriterion("payEndTime is null");
            return (Criteria) this;
        }

        public Criteria andPayEndTimeIsNotNull() {
            addCriterion("payEndTime is not null");
            return (Criteria) this;
        }

        public Criteria andPayEndTimeEqualTo(Long value) {
            addCriterion("payEndTime =", value, "payEndTime");
            return (Criteria) this;
        }

        public Criteria andPayEndTimeNotEqualTo(Long value) {
            addCriterion("payEndTime <>", value, "payEndTime");
            return (Criteria) this;
        }

        public Criteria andPayEndTimeGreaterThan(Long value) {
            addCriterion("payEndTime >", value, "payEndTime");
            return (Criteria) this;
        }

        public Criteria andPayEndTimeGreaterThanOrEqualTo(Long value) {
            addCriterion("payEndTime >=", value, "payEndTime");
            return (Criteria) this;
        }

        public Criteria andPayEndTimeLessThan(Long value) {
            addCriterion("payEndTime <", value, "payEndTime");
            return (Criteria) this;
        }

        public Criteria andPayEndTimeLessThanOrEqualTo(Long value) {
            addCriterion("payEndTime <=", value, "payEndTime");
            return (Criteria) this;
        }

        public Criteria andPayEndTimeIn(List<Long> values) {
            addCriterion("payEndTime in", values, "payEndTime");
            return (Criteria) this;
        }

        public Criteria andPayEndTimeNotIn(List<Long> values) {
            addCriterion("payEndTime not in", values, "payEndTime");
            return (Criteria) this;
        }

        public Criteria andPayEndTimeBetween(Long value1, Long value2) {
            addCriterion("payEndTime between", value1, value2, "payEndTime");
            return (Criteria) this;
        }

        public Criteria andPayEndTimeNotBetween(Long value1, Long value2) {
            addCriterion("payEndTime not between", value1, value2, "payEndTime");
            return (Criteria) this;
        }

        public Criteria andAssignmentStartTimeIsNull() {
            addCriterion("assignmentStartTime is null");
            return (Criteria) this;
        }

        public Criteria andAssignmentStartTimeIsNotNull() {
            addCriterion("assignmentStartTime is not null");
            return (Criteria) this;
        }

        public Criteria andAssignmentStartTimeEqualTo(Long value) {
            addCriterion("assignmentStartTime =", value, "assignmentStartTime");
            return (Criteria) this;
        }

        public Criteria andAssignmentStartTimeNotEqualTo(Long value) {
            addCriterion("assignmentStartTime <>", value, "assignmentStartTime");
            return (Criteria) this;
        }

        public Criteria andAssignmentStartTimeGreaterThan(Long value) {
            addCriterion("assignmentStartTime >", value, "assignmentStartTime");
            return (Criteria) this;
        }

        public Criteria andAssignmentStartTimeGreaterThanOrEqualTo(Long value) {
            addCriterion("assignmentStartTime >=", value, "assignmentStartTime");
            return (Criteria) this;
        }

        public Criteria andAssignmentStartTimeLessThan(Long value) {
            addCriterion("assignmentStartTime <", value, "assignmentStartTime");
            return (Criteria) this;
        }

        public Criteria andAssignmentStartTimeLessThanOrEqualTo(Long value) {
            addCriterion("assignmentStartTime <=", value, "assignmentStartTime");
            return (Criteria) this;
        }

        public Criteria andAssignmentStartTimeIn(List<Long> values) {
            addCriterion("assignmentStartTime in", values, "assignmentStartTime");
            return (Criteria) this;
        }

        public Criteria andAssignmentStartTimeNotIn(List<Long> values) {
            addCriterion("assignmentStartTime not in", values, "assignmentStartTime");
            return (Criteria) this;
        }

        public Criteria andAssignmentStartTimeBetween(Long value1, Long value2) {
            addCriterion("assignmentStartTime between", value1, value2, "assignmentStartTime");
            return (Criteria) this;
        }

        public Criteria andAssignmentStartTimeNotBetween(Long value1, Long value2) {
            addCriterion("assignmentStartTime not between", value1, value2, "assignmentStartTime");
            return (Criteria) this;
        }

        public Criteria andAssignmentEndTimeIsNull() {
            addCriterion("assignmentEndTime is null");
            return (Criteria) this;
        }

        public Criteria andAssignmentEndTimeIsNotNull() {
            addCriterion("assignmentEndTime is not null");
            return (Criteria) this;
        }

        public Criteria andAssignmentEndTimeEqualTo(Long value) {
            addCriterion("assignmentEndTime =", value, "assignmentEndTime");
            return (Criteria) this;
        }

        public Criteria andAssignmentEndTimeNotEqualTo(Long value) {
            addCriterion("assignmentEndTime <>", value, "assignmentEndTime");
            return (Criteria) this;
        }

        public Criteria andAssignmentEndTimeGreaterThan(Long value) {
            addCriterion("assignmentEndTime >", value, "assignmentEndTime");
            return (Criteria) this;
        }

        public Criteria andAssignmentEndTimeGreaterThanOrEqualTo(Long value) {
            addCriterion("assignmentEndTime >=", value, "assignmentEndTime");
            return (Criteria) this;
        }

        public Criteria andAssignmentEndTimeLessThan(Long value) {
            addCriterion("assignmentEndTime <", value, "assignmentEndTime");
            return (Criteria) this;
        }

        public Criteria andAssignmentEndTimeLessThanOrEqualTo(Long value) {
            addCriterion("assignmentEndTime <=", value, "assignmentEndTime");
            return (Criteria) this;
        }

        public Criteria andAssignmentEndTimeIn(List<Long> values) {
            addCriterion("assignmentEndTime in", values, "assignmentEndTime");
            return (Criteria) this;
        }

        public Criteria andAssignmentEndTimeNotIn(List<Long> values) {
            addCriterion("assignmentEndTime not in", values, "assignmentEndTime");
            return (Criteria) this;
        }

        public Criteria andAssignmentEndTimeBetween(Long value1, Long value2) {
            addCriterion("assignmentEndTime between", value1, value2, "assignmentEndTime");
            return (Criteria) this;
        }

        public Criteria andAssignmentEndTimeNotBetween(Long value1, Long value2) {
            addCriterion("assignmentEndTime not between", value1, value2, "assignmentEndTime");
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