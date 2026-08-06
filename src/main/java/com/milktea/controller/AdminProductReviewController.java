package com.milktea.controller;

import com.milktea.entity.ProductReview;
import com.milktea.repository.ProductReviewRepository;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/admin/product-reviews")
public class AdminProductReviewController {
    private final ProductReviewRepository reviewRepository;

    public AdminProductReviewController(ProductReviewRepository reviewRepository) {
        this.reviewRepository = reviewRepository;
    }

    @GetMapping
    public String list(Model model) {
        model.addAttribute("reviews", reviewRepository.findAll(Sort.by(Sort.Direction.DESC, "createdAt")));
        return "admin-product-review-list";
    }

    @GetMapping("/hide/{id}")
    public String hide(@PathVariable Integer id) {
        reviewRepository.findById(id).ifPresent(review -> {
            review.setStatus("HIDDEN");
            reviewRepository.save(review);
        });
        return "redirect:/admin/product-reviews";
    }

    @GetMapping("/show/{id}")
    public String show(@PathVariable Integer id) {
        reviewRepository.findById(id).ifPresent(review -> {
            review.setStatus("VISIBLE");
            reviewRepository.save(review);
        });
        return "redirect:/admin/product-reviews";
    }

    @GetMapping("/delete/{id}")
    public String delete(@PathVariable Integer id) {
        reviewRepository.deleteById(id);
        return "redirect:/admin/product-reviews";
    }
}
