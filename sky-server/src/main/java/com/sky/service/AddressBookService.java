package com.sky.service;

import com.sky.entity.AddressBook;

import java.util.List;

public interface AddressBookService {
    /**
     * 添加地址
     * @param addressBook
     */
    void addAddressBook(AddressBook addressBook);

    /**
     * 查询当前用户所有地址
     * @return
     */
    List<AddressBook> getAddressBookList();

    /**
     * 查询当前用户默认地址
     * @return
     */
    AddressBook getDefault();

    /**
     * 根据id查询地址
     * @param id
     * @return
     */
    AddressBook getAddressBookById(Long id);

    /**
     * 修改地址
     * @param addressBook
     */
    void updateAddressBook(AddressBook addressBook);

    /**
     * 设置默认地址
     * @param addressBook
     */
    void setDefault(AddressBook addressBook);

    /**
     * 删除地址
     * @param id
     */
    void deleteById(Integer id);
}
