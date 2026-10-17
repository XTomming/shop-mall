package com.niit.shopmall.service;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.niit.shopmall.common.BusinessException;
import com.niit.shopmall.dto.AddressDTO;
import com.niit.shopmall.entity.MemberAddress;
import com.niit.shopmall.mapper.MemberAddressMapper;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 收货地址服务
 */
@Service
public class AddressService {

    @Autowired
    private MemberAddressMapper addressMapper;

    public List<MemberAddress> list(Long memberId) {
        return addressMapper.selectList(Wrappers.<MemberAddress>lambdaQuery()
                .eq(MemberAddress::getMemberId, memberId)
                .orderByDesc(MemberAddress::getIsDefault)
                .orderByDesc(MemberAddress::getCreateTime));
    }

    @Transactional(rollbackFor = Exception.class)
    public void add(Long memberId, AddressDTO dto) {
        if (dto.getIsDefault() != null && dto.getIsDefault() == 1) {
            clearDefault(memberId);
        }
        MemberAddress address = new MemberAddress();
        BeanUtils.copyProperties(dto, address);
        address.setMemberId(memberId);
        if (address.getIsDefault() == null) {
            address.setIsDefault(0);
        }
        addressMapper.insert(address);
    }

    @Transactional(rollbackFor = Exception.class)
    public void update(Long memberId, Long id, AddressDTO dto) {
        MemberAddress exist = addressMapper.selectById(id);
        if (exist == null || !exist.getMemberId().equals(memberId)) {
            throw new BusinessException("地址不存在");
        }
        if (dto.getIsDefault() != null && dto.getIsDefault() == 1) {
            clearDefault(memberId);
        }
        MemberAddress address = new MemberAddress();
        BeanUtils.copyProperties(dto, address);
        address.setId(id);
        addressMapper.updateById(address);
    }

    public void delete(Long memberId, Long id) {
        MemberAddress exist = addressMapper.selectById(id);
        if (exist == null || !exist.getMemberId().equals(memberId)) {
            throw new BusinessException("地址不存在");
        }
        addressMapper.deleteById(id);
    }

    private void clearDefault(Long memberId) {
        MemberAddress update = new MemberAddress();
        update.setIsDefault(0);
        addressMapper.update(update, Wrappers.<MemberAddress>lambdaUpdate()
                .eq(MemberAddress::getMemberId, memberId)
                .eq(MemberAddress::getIsDefault, 1));
    }
}
