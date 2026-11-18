package com.scamshield.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.scamshield.entity.ScamReport;

public interface ScamReportRepository
        extends JpaRepository<ScamReport, Long> {

    List<ScamReport> findByReportedBy_EmailOrderByReportedAtDesc(
            String email);

    List<ScamReport> findByReportedBy_EmailAndStatusNotOrderByReportedAtDesc(
            String email, String status);

    List<ScamReport> findAllByOrderByReportedAtDesc();

    Page<ScamReport> findAllByOrderByReportedAtDesc(
            Pageable pageable);

    long countByStatus(String status);

    List<ScamReport> findByStatusOrderByReportedAtDesc(
            String status);

    Page<ScamReport> findByStatus(
            String status, Pageable pageable);

    Page<ScamReport> findByRiskLevel(
            String riskLevel, Pageable pageable);

    Page<ScamReport> findByStatusAndRiskLevel(
            String status,
            String riskLevel,
            Pageable pageable);

    long countByRiskLevel(String riskLevel);

    @Query("""
            SELECT COUNT(s)
            FROM ScamReport s
            WHERE s.suspiciousPhone = :phone
            AND s.status <> 'DELETED'
            """)
    long countActiveReportsByPhone(
            @Param("phone") String phone);

    @Query("""
            SELECT COUNT(DISTINCT s.reportedBy.id)
            FROM ScamReport s
            WHERE s.suspiciousPhone = :phone
            AND s.status <> 'DELETED'
            """)
    long countUniqueReportersByPhone(
            @Param("phone") String phone);

    @Query("""
            SELECT COUNT(s)
            FROM ScamReport s
            WHERE s.suspiciousPhone = :phone
            AND s.status = 'VERIFIED'
            """)
    long countVerifiedReportsByPhone(
            @Param("phone") String phone);

    List<ScamReport> findBySuspiciousPhoneOrderByReportedAtDesc(
            String suspiciousPhone);

    List<ScamReport> findBySuspiciousPhoneAndStatusNotOrderByReportedAtDesc(
            String suspiciousPhone,
            String status);

    @Query("""
            SELECT s.suspiciousPhone
            FROM ScamReport s
            WHERE s.suspiciousPhone IS NOT NULL
            AND TRIM(s.suspiciousPhone) <> ''
            AND s.status <> 'DELETED'
            GROUP BY s.suspiciousPhone
            HAVING COUNT(DISTINCT s.reportedBy.id) >= :minimumReporters
            ORDER BY COUNT(s) DESC
            """)
    List<String> findPubliclyReportedPhones(
            @Param("minimumReporters") long minimumReporters);

    @Query("""
            SELECT s.incidentCity, s.scamType, COUNT(s)
            FROM ScamReport s
            WHERE UPPER(TRIM(s.incidentCity))
                    = UPPER(TRIM(:city))
            AND s.status = 'VERIFIED'
            AND s.incidentCity IS NOT NULL
            GROUP BY s.incidentCity, s.scamType
            ORDER BY COUNT(s) DESC
            """)
    List<Object[]> findVerifiedScamAlertsByCity(
            @Param("city") String city);
}