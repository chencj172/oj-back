package com.chencj.pk.model.po;

import java.io.Serializable;
import java.time.LocalDateTime;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * pk_record
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class PkRecord implements Serializable {
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 记录生成时间
     */
    private LocalDateTime createTime;

    /**
     * 关联的用户ID
     */
    private Integer uid;

    /**
     * pk结果
     */
    private String result;

    private static final long serialVersionUID = 1L;
}