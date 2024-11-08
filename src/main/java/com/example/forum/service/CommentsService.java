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
            comments.add(comment);
        }
        return comments;
    }
}
