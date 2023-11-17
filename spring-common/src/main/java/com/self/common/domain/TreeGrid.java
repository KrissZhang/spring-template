package com.self.common.domain;

import com.google.common.collect.Lists;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

/**
 * 树形节点
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TreeGrid<T> implements Serializable {

    private static final long serialVersionUID = 2765088142013764965L;

    private Long id;

    private Long parentId;

    private List<T> children = Lists.newArrayList();

}
