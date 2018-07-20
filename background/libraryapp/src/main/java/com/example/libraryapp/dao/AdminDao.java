package com.example.libraryapp.dao;

import com.example.libraryapp.dto.Admin;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AdminDao extends JpaRepository<Admin, String> {
}
