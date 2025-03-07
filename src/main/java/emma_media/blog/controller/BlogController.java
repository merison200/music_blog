package emma_media.blog.controller;

import emma_media.blog.dto.BlogResponseDto;
import emma_media.blog.exception.BlogNotFoundException;
import emma_media.blog.exception.UnauthorizedException;
import emma_media.blog.service.BlogService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/blogs")
@RequiredArgsConstructor
public class BlogController {

    @Autowired
    private BlogService blogService;

    @PostMapping
    public ResponseEntity<BlogResponseDto> createBlog(
            @RequestParam("title") String title,
            @RequestParam("content") String content,
            @RequestParam(value = "image", required = false) MultipartFile image) {

        try {
            BlogResponseDto blogResponse = blogService.createBlog(title, content, image);
            return ResponseEntity.status(HttpStatus.CREATED).body(blogResponse);
        } catch (UnauthorizedException ex) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
    }

    @PutMapping("/{blogId}")
    public ResponseEntity<BlogResponseDto> updateBlog(
            @PathVariable Long blogId,
            @RequestParam("title") String title,
            @RequestParam("content") String content,
            @RequestParam(value = "image", required = false) MultipartFile image) {

        try {
            BlogResponseDto blogResponse = blogService.updateBlog(blogId, title, content, image);
            return ResponseEntity.ok(blogResponse);
        } catch (BlogNotFoundException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        } catch (UnauthorizedException ex) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
    }

    @DeleteMapping("/{blogId}")
    public ResponseEntity<Void> deleteBlog(@PathVariable Long blogId) {
        try {
            blogService.deleteBlog(blogId);
            return ResponseEntity.noContent().build();
        } catch (BlogNotFoundException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        } catch (UnauthorizedException ex) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    @GetMapping("/my-blogs")
    public ResponseEntity<List<BlogResponseDto>> getUserBlogs() {
        try {
            List<BlogResponseDto> blogs = blogService.getBlogsByUser();
            return ResponseEntity.ok(blogs);
        } catch (UnauthorizedException ex) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @GetMapping
    public ResponseEntity<List<BlogResponseDto>> getAllBlogs() {
        try {
            List<BlogResponseDto> blogs = blogService.getAllBlogs();
            return ResponseEntity.ok(blogs);
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @GetMapping("/{blogId}")
    public ResponseEntity<BlogResponseDto> getBlogById(@PathVariable Long blogId) {
        try {
            BlogResponseDto blog = blogService.getBlogById(blogId);
            return ResponseEntity.ok(blog);
        } catch (BlogNotFoundException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }
}