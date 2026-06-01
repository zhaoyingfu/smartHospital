package com.smarthospital.service.rule;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.smarthospital.dal.mapper.BizRuleMapper;
import com.smarthospital.model.entity.BizRule;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BizRuleService {

    private final BizRuleMapper bizRuleMapper;

    public String getRuleValue(String ruleKey, Long kioskId) {
        if (kioskId != null) {
            BizRule machineRule = bizRuleMapper.selectOne(
                    new LambdaQueryWrapper<BizRule>()
                            .eq(BizRule::getRuleKey, ruleKey)
                            .eq(BizRule::getScope, "MACHINE")
                            .eq(BizRule::getScopeId, kioskId));
            if (machineRule != null) {
                return machineRule.getRuleValue();
            }
        }
        BizRule globalRule = bizRuleMapper.selectOne(
                new LambdaQueryWrapper<BizRule>()
                        .eq(BizRule::getRuleKey, ruleKey)
                        .eq(BizRule::getScope, "GLOBAL"));
        return globalRule != null ? globalRule.getRuleValue() : null;
    }

    public int getIntRule(String ruleKey, Long kioskId, int defaultValue) {
        String value = getRuleValue(ruleKey, kioskId);
        return value != null ? Integer.parseInt(value) : defaultValue;
    }

    public List<BizRule> listAll() {
        return bizRuleMapper.selectList(
                new LambdaQueryWrapper<BizRule>().orderByAsc(BizRule::getRuleKey, BizRule::getScope));
    }

    public void updateRule(Long ruleId, String ruleValue) {
        BizRule rule = bizRuleMapper.selectById(ruleId);
        if (rule != null) {
            rule.setRuleValue(ruleValue);
            bizRuleMapper.updateById(rule);
        }
    }
}