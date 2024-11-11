package com.example.forum.service;

import com.example.forum.controller.form.CommentsForm;
import com.example.forum.controller.form.ReportForm;
import com.example.forum.repository.CommentsRepository;
import com.example.forum.repository.ReportRepository;
import com.example.forum.repository.entity.Comments;
import com.example.forum.repository.entity.Report;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class CommentsService {
    @Autowired
    CommentsRepository commentsRepository;

    /*
     * コメント全件取得処理
     */
    public List<CommentsForm> findAllComments() {
        List<Comments> results = commentsRepository.findAll();
        List<CommentsForm> comments = setCommentsForm(results);
        return comments;
    }
    /*
     * DBから取得したデータをFormに設定
     */
    private List<CommentsForm> setCommentsForm(List<Comments> results) {
        List<CommentsForm> comments = new ArrayList<>();

        for (int i = 0; i < results.size(); i++) {
            CommentsForm comment = new CommentsForm();
            Comments result = results.get(i);
            comment.setId(result.getId());
            comment.setContent(result.getContent());
            comment.setReportId(result.getReportId());
            comments.add(comment);
        }
        return comments;
    }

    /*
     * レコード追加
     */
    public void saveComments(CommentsForm reqComments) {
        Comments saveComments = setCommentsEntity(reqComments);
        commentsRepository.save(saveComments);
    }

    /*
     * リクエストから取得した情報をEntityに設定
     */
    private Comments setCommentsEntity(CommentsForm reqComments) {
        Comments comments = new Comments();
        comments.setId(reqComments.getId());
        comments.setContent(reqComments.getContent());
        comments.setReportId(reqComments.getReportId());
        return comments;
    }

    //レコード1件取得
    public CommentsForm editComments(Integer id){
        List<Comments> results = new ArrayList<>();
        results.add((Comments) commentsRepository.findById(id).orElse(null));
        List<CommentsForm> comments = setCommentsForm(results);
        return comments.get(0);
    }

    /*
     * レコード削除
     */
    public void deleteComments(Integer id) {
        commentsRepository.deleteById(id);
    }
}
