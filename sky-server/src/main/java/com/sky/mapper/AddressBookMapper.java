package com.sky.mapper;

import com.sky.entity.AddressBook;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface AddressBookMapper {


    /**
     * 添加地址
     *
     * @param addressBook1
     */
    @Insert("insert into address_book (user_id, consignee, sex, phone, province_code, province_name, city_code, city_name, district_code, district_name, detail, label, is_default) VALUES " +
            "(#{userId},#{consignee},#{sex},#{phone},#{provinceCode},#{provinceName},#{cityCode},#{cityName},#{districtCode},#{districtName},#{detail},#{label},#{isDefault})")
    void addAddressBook(AddressBook addressBook1);

    /**
     * 查询地址列表
     *
     * @param userId
     * @return
     */
    @Select("select * from address_book where user_id = #{userId}")
    List<AddressBook> getAddressBookList(Long userId);

    /**
     * 查询当前默认地址
     *
     * @param userId
     * @param defaultId
     * @return
     */
    @Select("select * from address_book where user_id = #{userId} and is_default = #{defaultId}")
    AddressBook getDefault(Long userId, Long defaultId);

    /**
     * 根据id查询地址
     *
     * @param id
     * @return
     */
    @Select("select * from address_book where id = #{id}")
    AddressBook getAddressBookById(Long id);

    /**
     * 修改地址
     *
     * @param addressBook
     */
    void update(AddressBook addressBook);

    /**
     * 删除地址
     *
     * @param id
     */
    @Delete("delete from address_book where id = #{id}")
    void delete(Integer id);
}
