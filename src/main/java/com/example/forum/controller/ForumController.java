package com.example.forum.controller;

import com.example.forum.controller.form.CommentsForm;
import com.example.forum.controller.form.ReportForm;
import com.example.forum.repository.entity.Comments;
import com.example.forum.service.CommentsService;
import com.example.forum.service.ReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;

@Controller
public class ForumController {
    @Autowired
    ReportService reportService;
    CommentsService commentsService;
    /*
     * 投稿内容表示処理
     */
    @GetMapping
    public ModelAndView top() {
        ModelAndView mav = new ModelAndView();
        // 投稿・返信を全件取得
        List<ReportForm> contentData = reportService.findAllReport();
        List<CommentsForm> commentsData = commentsService.findAllComments();
        // 画面遷移先を指定
        mav.setViewName("/top");
        // 投稿データオブジェクトを保管
        mav.addObject("contents", contentData);
        mav.addObject("comments", commentsData);
        return mav;
    }



    /*
     * 新規投稿画面表示
     */
    @GetMapping("/new")
    public ModelAndView newContent() {
        ModelAndView mav = new ModelAndView();
        // form用の空のentityを準備
        ReportForm reportForm = new ReportForm();
        // 画面遷移先を指定
        mav.setViewName("/new");
        // 準備した空のFormを保管
        mav.addObject("formModel", reportForm);
        return mav;
    }

    /*
     * 新規投稿処理
     */
    @PostMapping("/add")
    public ModelAndView addContent(@ModelAttribute("formModel") ReportForm reportForm){
        // 投稿をテーブルに格納
        reportService.saveReport(reportForm);
        // rootへリダイレクト
        return new ModelAndView("redirect:/");
    }

    /*
     * 投稿削除処理
     */
    @DeleteMapping("/delete/{id}")
    public ModelAndView deleteContent(@PathVariable Integer id){
        // 投稿をテーブルから削除
        reportService.deleteReport(id);
        //rootへリダイレクト
        return new ModelAndView("redirect:/");
    }

    //投稿編集画面に遷移
    @GetMapping("/edit/{id}")
    public ModelAndView editContent(@PathVariable Integer id) {
        ModelAndView mav = new ModelAndView();
        //投稿の検索、抽出
        ReportForm report = reportService.editReport(id);
        // 画面遷移先を指定
        mav.setViewName("/edit");
        // Formを詰めて編集画面に遷移
        mav.addObject("formModel", report);
        return mav;
    }

    /*
     * 投稿編集処理
     */
    @PostMapping("/update/{id}")
    public ModelAndView updateContent(@PathVariable Integer id, @ModelAttribute("formModel") ReportForm report){
        // UrlParameterのidを更新するentityにセット
        report.setId(id);
        // 投稿を更新
        reportService.saveReport(report);
        // rootへリダイレクト
        return new ModelAndView("redirect:/");
    }
}