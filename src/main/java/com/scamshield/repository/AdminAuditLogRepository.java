
package com.scamshield.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.scamshield.entity.AdminAuditLog;

public interface AdminAuditLogRepository
        extends JpaRepository<AdminAuditLog, Long> {

    List<AdminAuditLog> findAllByOrderByActionAtDesc();

    List<AdminAuditLog> findByReportIdOrderByActionAtDesc(Long reportId);

    List<AdminAuditLog> findByAdminEmailOrderByActionAtDesc(String adminEmail);
}
