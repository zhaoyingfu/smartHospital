package com.smarthospital.web.controller.admin;

import com.smarthospital.common.response.R;
import com.smarthospital.model.entity.BizRule;
import com.smarthospital.service.rule.BizRuleService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin/rule")
@RequiredArgsConstructor
public class AdminRuleController {

    private final BizRuleService bizRuleService;

    @GetMapping("/list")
    public R<List<BizRule>> list() {
        return R.ok(bizRuleService.listAll());
    }

    @PutMapping("/{id}")
    public R<Void> update(@PathVariable Long id, @RequestParam String ruleValue) {
        bizRuleService.updateRule(id, ruleValue);
        return R.ok();
    }
}