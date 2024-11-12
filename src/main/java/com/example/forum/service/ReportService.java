package com.example.forum.service;

import com.example.forum.controller.form.ReportForm;
import com.example.forum.repository.ReportRepository;
import com.example.forum.repository.entity.Report;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
public class ReportService {
    @Autowired
    ReportRepository reportRepository;

    /*
     * レコード全件取得処理
     */
    public List<ReportForm> findByCreatedDateBetweenOrderByUpdatedDateDesc(String start, String end) throws ParseException {
        // 投稿を絞り込んで取得
        String defaultStart = "2020-01-01 00:00:00";
        Date nowDate = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String defaultEnd = sdf.format(nowDate);
        String startParam = null;
        String endParam = null;

        if (StringUtils.hasText(start)) {
            startParam = start + " " + "00:00:00";
        } else {
            startParam = defaultStart;
        }
        if (StringUtils.hasText(end)) {
            endParam = end + " " + "23:59:59";
        } else {
            endParam = defaultEnd;
        }

        Date startDate = sdf.parse(startParam);
        Date endDate = sdf.parse(endParam);
        List<Report> results = reportRepository.findByCreatedDateBetweenOrderByUpdatedDateDesc(startDate, endDate);
        List<ReportForm> reports = setReportForm(results);
        return reports;
    }

    /*
     * DBから取得したデータをFormに設定
     */
    private List<ReportForm> setReportForm(List<Report> results) {
        List<ReportForm> reports = new ArrayList<>();

        for (int i = 0; i < results.size(); i++) {
            ReportForm report = new ReportForm();
            Report result = results.get(i);
            report.setId(result.getId());
            report.setContent(result.getContent());
            report.setCreatedDate(result.getCreatedDate());
            report.setUpdatedDate(result.getUpdatedDate());
            reports.add(report);
        }
        return reports;
    }

    /*
     * レコード追加
     */
    public void saveReport(ReportForm reqReport) throws ParseException {
        Report saveReport = setReportEntity(reqReport);
        reportRepository.save(saveReport);
    }

    /*
     * リクエストから取得した情報をEntityに設定
     */
    private Report setReportEntity(ReportForm reqReport) throws ParseException {
        Report report = new Report();
        report.setId(reqReport.getId());
        report.setContent(reqReport.getContent());

        Date nowDate = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String currentTime = sdf.format(nowDate);
        report.setUpdatedDate(sdf.parse(currentTime));
        if (reqReport.getCreatedDate() == null) {
            report.setCreatedDate(sdf.parse(currentTime));
        } else {
            report.setCreatedDate(reqReport.getCreatedDate());
        }
        return report;
    }

    /*
     * レコード削除
     */
    public void deleteReport(Integer id) {
        reportRepository.deleteById(id);
    }

    //レコード1件取得
    public ReportForm editReport(Integer id){
        List<Report> results = new ArrayList<>();
        results.add((Report) reportRepository.findById(id).orElse(null));
        List<ReportForm> reports = setReportForm(results);
        return reports.get(0);
    }
}