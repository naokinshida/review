package com.example.nagoyameshi.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.nagoyameshi.entity.Review;
import com.example.nagoyameshi.entity.Storeinfo;
import com.example.nagoyameshi.repository.StoreinfoRepository;
import com.example.nagoyameshi.service.ReviewService;

@Controller
public class ReviewController {

    @Autowired
    private StoreinfoRepository storeInfoRepository;

    @Autowired
    private ReviewService reviewService;

    @GetMapping("/review/{id}")
    public String getStoreinfo(@PathVariable("id") Integer id, Model model) {
        Storeinfo storeinfo = storeInfoRepository.getReferenceById(id);
        List<Review> reviews = reviewService.findByStoreId(id);
        double averageScore = reviews.stream().mapToInt(Review::getStar).average().orElse(0.0);

        model.addAttribute("storeinfo", storeinfo);
        model.addAttribute("reviews", reviews);
        model.addAttribute("averageScore", averageScore);

        return "reviews/review";
    }

    @PostMapping("/review")
    public String postReview(
            @RequestParam("storeId") int storeId,
            @RequestParam("comment") String comment,
            @RequestParam("star") int star) {

        Review newReview = new Review();
        newReview.setStoreId(storeId);
        newReview.setComment(comment);
        newReview.setStar(star);

        reviewService.save(newReview);
        return "redirect:/storeinfo/" + storeId; // レビュー送信後に店舗詳細画面にリダイレクトする
    }
    
 // レビュー編集画面を表示
     @GetMapping("/reviews/user_reviews")
    public String listUserReviews(Model model) {
        List<Review> reviews = reviewService.findAll();
        model.addAttribute("reviews", reviews);
        return "reviews/user_reviews"; // テンプレートファイルのパス
    }


    @PostMapping("/review/update")
    public String updateReview(
            @RequestParam("id") int id,
            @RequestParam("comment") String comment,
            @RequestParam("star") int star) {

        Review review = reviewService.findById(id);
        review.setComment(comment);
        review.setStar(star);
        reviewService.save(review);

        return "redirect:/user/reviews";  // 更新後にレビュー一覧ページにリダイレクト
    }
    @PostMapping("/review/delete/{id}")
    public String deleteReview(@PathVariable("id") int id) {
        reviewService.deleteReview(id);
        return "redirect:/user/reviews";  // 削除後にレビュー一覧ページにリダイレクト
    }
}