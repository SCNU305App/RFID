package com.example.libraryapp.service;

import com.example.libraryapp.dao.AdminDao;
import com.example.libraryapp.dto.Admin;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class AdminServicelmpl {
    @Autowired
    AdminDao adminDao;

    @Override
    public Map<String,Object> register(String aacconut, String apassword){
        Map<String,Object>map = new HashMap<>();
        Admin admin = new Admin();
        admin.setAaccount(aacconut);
        admin.setAapassword(apassword);
        adminDao.save(admin);
        map.put("success",1);
        return map;
    }

}
