package com.firstteam.sportsLink.qna;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@Controller
public class QnaController {

    private final QnaService qnaService;
    private final CommentService commentService;

    @Autowired
    public QnaController(QnaService qnaService, CommentService commentService) {
        this.qnaService = qnaService;
        this.commentService = commentService;
    }

    @PostMapping("/qna/save")
    public String saveQna(@ModelAttribute QnaDTO qnaDTO) {
        qnaService.saveQna(qnaDTO);
        return "redirect:/qna-list"; // 저장 후 목록 페이지로 리다이렉트
    }

    @GetMapping("/qna-list")
    public String qna(Model model) {
        // 서비스를 통해 모든 문의사항을 가져옵니다.
        List<QnaDTO> inquiries = qnaService.getAllInquiries();

        // 모델에 문의사항 목록을 추가합니다.
        model.addAttribute("inquiries", inquiries);

        // qna.html 파일을 반환합니다.
        return "qna/qna";
    }

    // 글쓰기 페이지를 반환하는 메서드 추가
    @GetMapping("/qna/new")
    public String newQna(Model model) {
        model.addAttribute("qnaDTO", new QnaDTO());
        return "qna/qna_write"; // qna_write.html로 매핑
    }

    @GetMapping("/qna_inner/{id}")
    public String qnainner(@PathVariable("id") Long id, Model model) {
        if (id == null) {
            // id가 null이면 404 오류 반환
            return "error/404";
        }

        QnaDTO inquiry = qnaService.getInquiryById(id);
        if (inquiry == null) {
            // 해당 ID에 대한 문의사항이 없으면 404 오류 반환
            return "error/404";
        }

        // 문의사항과 댓글을 모델에 추가하여 페이지 반환
        List<CommentDTO> comments = commentService.getCommentsByInquiryId(id);
        model.addAttribute("inquiry", inquiry);
        model.addAttribute("comments", comments);
        model.addAttribute("newComment", new CommentDTO());
        model.addAttribute("isAdmin", true);

        return "qna/qna_inner";
    }

//    // 관리자만 댓글 달기
//    @GetMapping("/qna_inner/{id}")
//    public String qnainner(@PathVariable("id") Long id, Model model, HttpSession session) {
//
//        // 세션에서 로그인한 사용자의 정보 가져오기
//        UserDTO user = (UserDTO) session.getAttribute("user");
//
//        if (user != null && user.isAdmin()) { // 관리자인 경우에만 댓글 폼을 노출
//            model.addAttribute("isAdmin", true);
//        }
//
//        if (id == null) {
//            // id가 null이면 404 오류 반환
//            return "error/404";
//        }
//
//        QnaDTO inquiry = qnaService.getInquiryById(id);
//        if (inquiry == null) {
//            // 해당 ID에 대한 문의사항이 없으면 404 오류 반환
//            return "error/404";
//        }
//
//        // 문의사항과 댓글을 모델에 추가하여 페이지 반환
//        List<CommentDTO> comments = commentService.getCommentsByInquiryId(id);
//        model.addAttribute("inquiry", inquiry);
//        model.addAttribute("comments", comments);
//        model.addAttribute("newComment", new CommentDTO());
//        model.addAttribute("isAdmin", true);
//
//        return "qna/qna_inner";
//    }


    @PostMapping("/addComment")
    public String addComment(@ModelAttribute("newComment") CommentDTO newComment) {
        Long inquiryId = newComment.getInquiryId(); // 댓글이 속한 문의사항의 ID


        // 댓글 저장
        commentService.saveComment(newComment);

        // 해당 문의사항 페이지로 리다이렉트
        return "redirect:/qna_inner/" + inquiryId;
    }
}
