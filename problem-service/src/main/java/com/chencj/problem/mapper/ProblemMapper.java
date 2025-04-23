package com.chencj.problem.mapper;


import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.chencj.problem.model.po.Problem;
import com.chencj.problem.model.vo.ProblemVo;

public interface ProblemMapper extends BaseMapper<Problem> {
    IPage<ProblemVo> search(Page<ProblemVo> page, Integer level, String word, Integer uid);
}