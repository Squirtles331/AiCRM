package com.aicrm.module.tag.mapper;

import com.aicrm.module.customer.entity.Customer;
import com.aicrm.module.tag.entity.CustomerTagRel;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

/**
 * 客户-标签关联 Mapper
 */
public interface CustomerTagRelMapper extends BaseMapper<CustomerTagRel> {

    /**
     * 按标签分页查询客户（标签筛选）
     */
    @Select("SELECT c.* FROM customer c " +
            "JOIN customer_tag_rel r ON r.customer_id = c.id AND r.tag_id = #{tagId} " +
            "WHERE c.deleted = 0 ORDER BY c.id DESC")
    IPage<Customer> pageCustomersByTag(IPage<Customer> page, @Param("tagId") Long tagId);
}
