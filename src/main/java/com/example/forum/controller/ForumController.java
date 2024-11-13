package com.example.forum.controller;

import com.example.forum.controller.form.CommentsForm;
import com.example.forum.controller.form.ReportForm;
import com.example.forum.repository.entity.Comments;
import com.example.forum.service.CommentsService;
import com.example.forum.service.ReportService;

import jakarta.servlet.http.HttpSession;
import lombok.SneakyThrows;
import org.hibernate.jpa.internal.util.PersistenceUtilHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.util.StringUtils;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.text.SimpleDateFormat;

@Controller
public class ForumController {
    @Autowired
    HttpSession session;

    @Autowired
    ReportService reportService;

    @Autowired
    CommentsService commentsService;



    /*
     * 投稿・返信内容表示処理
     */
    @GetMapping
    public ModelAndView top(@RequestParam(name = "start", required = false) String start,
                            @RequestParam(name = "end", required = false) String end) throws ParseException {
        ModelAndView mav = new ModelAndView();
        //投稿を絞り込んで表示
        List<ReportForm> contentData = reportService.findByCreatedDateBetweenOrderByUpdatedDateDesc(start, end);
        // 返信を全件表示
        List<CommentsForm> commentsData = commentsService.findAllComments();
        // 空の返信欄を追加
        CommentsForm commentsForm = new CommentsForm();
        // 画面遷移先を指定
        mav.setViewName("/top");
        // 投稿データオブジェクトを保管
        mav.addObject("contents", contentData);
        mav.addObject("comments", commentsData);
        mav.addObject("formModel", commentsForm);
        //エラーメッセージ表示
        List<String> errorMessage = (List<String>) session.getAttribute("errorMessages");
        if (errorMessage != null){
            Integer reportId = (Integer) session.getAttribute("reportId");
            mav.addObject("reportId", reportId);
            mav.addObject("errorMessages", errorMessage);
            session.invalidate();
        }
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
        //エラーメッセージ表示
        List<String> errorMessage = (List<String>) session.getAttribute("errorMessages");
        if (errorMessage != null){
            mav.addObject("errorMessages", errorMessage);
            session.invalidate();
        }
        return mav;
    }

    /*
     * 新規投稿処理
     */
    @PostMapping("/add")
    public ModelAndView addContent(@ModelAttribute("formModel") @Validated ReportForm reportForm, BindingResult result) throws ParseException {
        //バリデーションを実行
        List<String> errorMessages = new ArrayList<String>();
        if (!isValid(result, errorMessages)) {
            session.setAttribute("errorMessages", errorMessages);
            return new ModelAndView("redirect:/new");
        }

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
        //エラーメッセージ表示
        List<String> errorMessage = (List<String>) session.getAttribute("errorMessages");
        if (errorMessage != null){
            mav.addObject("errorMessages", errorMessage);
            session.invalidate();
        }
        return mav;
    }

    /*
     * 投稿編集処理
     */
    @PostMapping("/update/{id}")
    public ModelAndView updateContent(@PathVariable Integer id, @ModelAttribute("formModel") @Validated ReportForm report,
                                      BindingResult result) throws ParseException {
        //バリデーション実行
        List<String> errorMessages = new ArrayList<String>();
        if (!isValid(result, errorMessages)) {
            session.setAttribute("errorMessages", errorMessages);
            return new ModelAndView("redirect:/edit/{id}");
        }
        // UrlParameterのidを更新するentityにセット
        report.setId(id);
        // 投稿を更新
        reportService.saveReport(report);
        // rootへリダイレクト
        return new ModelAndView("redirect:/");
    }

    /*
     * 新規返信処理
     */
    @PostMapping("/addComments/{reportId}/{reportContent}")
    public ModelAndView addComments(@PathVariable Integer reportId, @PathVariable String reportContent,
                                    @ModelAttribute("formModel") @Validated CommentsForm commentsForm, BindingResult result) throws ParseException {
        //バリデーション実行
        List<String> errorMessages = new ArrayList<String>();
        if (!isValid(result, errorMessages)) {
            session.setAttribute("errorMessages", errorMessages);
            session.setAttribute("reportId", reportId);
            return new ModelAndView("redirect:/");
        }
        //コメントに投稿ID・作成日・更新日をセット
        commentsForm.setReportId(reportId);
        Date nowDate = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String currentTime = sdf.format(nowDate);
        Date currentDate = sdf.parse(currentTime);
        commentsForm.setCreatedDate(currentDate);
        commentsForm.setUpdatedDate(currentDate);
        // 返信をテーブルに格納
        commentsService.saveComments(commentsForm);

        //投稿の更新日を更新
        ReportForm reportForm = new ReportForm();
        reportForm.setId(reportId);
        reportForm.setContent(reportContent);
        reportForm.setUpdatedDate(currentDate);
        reportService.saveReport(reportForm);
        // rootへリダイレクト
        return new ModelAndView("redirect:/");
    }

    //コメント編集画面に遷移
    @GetMapping("/editComments/{id}")
    public ModelAndView editComments(@PathVariable Integer id) {
        ModelAndView mav = new ModelAndView();
        //コメントの検索、抽出
        CommentsForm comments = commentsService.editComments(id);
        // 画面遷移先を指定
        mav.setViewName("/editComments");
        // Formを詰めて編集画面に遷移
        mav.addObject("formModel", comments);
        //エラーメッセージ表示
        List<String> errorMessage = (List<String>) session.getAttribute("errorMessages");
        if (errorMessage != null){
            mav.addObject("errorMessages", errorMessage);
            session.invalidate();
        }
        return mav;
    }

    /*
     * コメント編集処理
     */
    @PostMapping("/updateComments/{id}/{reportId}")
    public ModelAndView updateComments(@PathVariable Integer id, @PathVariable Integer reportId,
                                       @ModelAttribute("formModel") @Validated CommentsForm comments, BindingResult result) throws ParseException {
        //バリデーション実行
        List<String> errorMessages = new ArrayList<String>();
        if (!isValid(result, errorMessages)) {
            session.setAttribute("errorMessages", errorMessages);
            return new ModelAndView("redirect:/editComments/{id}");
        }

        //現在の日付
        Date nowDate = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String currentTime = sdf.format(nowDate);
        Date currentDate = sdf.parse(currentTime);

        // UrlParameterのid、report_idを更新するentityにセット
        comments.setId(id);
        comments.setReportId(reportId);
        comments.setUpdatedDate(currentDate);
        // 投稿を更新
        commentsService.saveComments(comments);
        //投稿の検索・更新
        ReportForm report = reportService.editReport(reportId);
        report.setUpdatedDate(currentDate);
        reportService.saveReport(report);
        // rootへリダイレクト
        return new ModelAndView("redirect:/");
    }

    /*
     * 投稿削除処理
     */
    @DeleteMapping("/deleteComments/{id}")
    public ModelAndView deleteComments(@PathVariable Integer id){
        // 投稿をテーブルから削除
        commentsService.deleteComments(id);
        //rootへリダイレクト
        return new ModelAndView("redirect:/");
    }

    //バリデーションメソッド
    private boolean isValid(BindingResult result, List<String> errorMessages) {
        if (result.hasErrors()) {
            for (FieldError error : result.getFieldErrors()) {
                String message = error.getDefaultMessage();
                errorMessages.add(message);
            }
            return false;
        } else {
            return true;
        }
    }
}