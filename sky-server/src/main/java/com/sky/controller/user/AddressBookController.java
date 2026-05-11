package com.sky.controller.user;

import com.sky.entity.AddressBook;
import com.sky.result.Result;
import com.sky.service.AddressBookService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/user/addressBook")
@Slf4j
@Api(tags = "C端-地址簿接口")
public class AddressBookController {

    @Autowired
    private AddressBookService addressBookService;

    /**
     * 添加地址
     * @param addressBook
     * @return
     */
    @PostMapping
    @ApiOperation("添加地址")
    public Result addAddressBook(@RequestBody AddressBook addressBook){
        log.info("添加地址：{}", addressBook);
        addressBookService.addAddressBook(addressBook);
        return Result.success();
    }

    /**
     * 查询当前用户所有地址
     * @return
     */
    @GetMapping("/list")
    @ApiOperation("查看当前用户所有地址")
    public Result<List<AddressBook>> getAddressBookList(){
        log.info("查看当前用户所有地址");
        return Result.success(addressBookService.getAddressBookList());
    }

    /**
     * 查询默认地址
     * @return
     */
    @GetMapping("/default")
    @ApiOperation("查询默认地址")
    public Result<AddressBook> getDefault(){
        log.info("查询默认地址");
        return Result.success(addressBookService.getDefault());
    }

    /**
     * 根据id查询地址
     * @param id
     * @return
     */
    @GetMapping("/{id}")
    @ApiOperation("查询地址")
    public Result<AddressBook> getAddressBookById(@PathVariable Long id){
        log.info("查询地址：{}", id);
        return Result.success(addressBookService.getAddressBookById(id));
    }

    /**
     * 修改地址
     * @param addressBook
     * @return
     */
    @PutMapping
    @ApiOperation("修改地址")
    public Result updateAddressBook(@RequestBody AddressBook addressBook) {
        log.info("修改地址：{}", addressBook);
        addressBookService.updateAddressBook(addressBook);
        return Result.success();
    }

    /**
     * 设置默认地址
     * @param addressBook
     * @return
     */
    @PutMapping("/default")
    @ApiOperation("设置默认地址")
    public Result setDefault(@RequestBody AddressBook addressBook) {
        log.info("设置默认地址：{}", addressBook.getId());
        addressBookService.setDefault(addressBook);
        return Result.success();
    }
    /**
     * 删除地址
     * @param id
     * @return
     */
    @DeleteMapping()
    @ApiOperation("删除地址")
    public Result deleteAddressBook(Integer id) {
        log.info("删除地址：{}", id);
        addressBookService.deleteById(id);
        return Result.success();
    }

}
