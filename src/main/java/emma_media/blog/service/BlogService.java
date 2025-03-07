package emma_media.blog.service;

import emma_media.blog.dto.BlogResponseDto;
import emma_media.blog.entity.Blog;
import emma_media.blog.exception.BlogNotFoundException;
import emma_media.blog.exception.UnauthorizedException;
import emma_media.blog.repository.BlogRepository;
import emma_media.user.dto.UserResponseDto;
import emma_media.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BlogService {

    @Autowired
    private BlogRepository blogRepository;
    @Autowired
    private UserService userService;

    public BlogResponseDto createBlog(String title, String content, MultipartFile image) {
        UserResponseDto currentUser = userService.getCurrentUser();
        if (currentUser == null) {
            throw new UnauthorizedException("User not authenticated");
        }

        Blog blog = new Blog();
        blog.setTitle(title);
        blog.setContent(content);
        blog.setUser(userService.getUser(currentUser.getId()));

        if (image != null && !image.isEmpty()) {
            String imageUrl = saveFile(image);
            blog.setImageUrl(imageUrl);
        }

        Blog savedBlog = blogRepository.save(blog);
        return mapToResponseDto(savedBlog);
    }

    public BlogResponseDto updateBlog(Long blogId, String title, String content, MultipartFile image) {
        UserResponseDto currentUser = userService.getCurrentUser();
        if (currentUser == null) {
            throw new UnauthorizedException("User not authenticated");
        }

        Blog blog = blogRepository.findById(blogId)
                .orElseThrow(() -> new BlogNotFoundException("Blog not found with id: " + blogId));

        if (!blog.getUser().getId().equals(currentUser.getId())) {
            throw new UnauthorizedException("You are not authorized to update this blog.");
        }

        blog.setTitle(title);
        blog.setContent(content);

        if (image != null && !image.isEmpty()) {
            deleteFile(blog.getImageUrl());

            String imageUrl = saveFile(image);
            blog.setImageUrl(imageUrl);
        }

        Blog updatedBlog = blogRepository.save(blog);
        return mapToResponseDto(updatedBlog);
    }

    public void deleteBlog(Long blogId) {
        UserResponseDto currentUser = userService.getCurrentUser();
        if (currentUser == null) {
            throw new UnauthorizedException("User not authenticated");
        }

        Blog blog = blogRepository.findById(blogId)
                .orElseThrow(() -> new BlogNotFoundException("Blog not found with id: " + blogId));

        if (!blog.getUser().getId().equals(currentUser.getId())) {
            throw new UnauthorizedException("You are not authorized to delete this blog.");
        }

        deleteFile(blog.getImageUrl());

        blogRepository.delete(blog);
    }

    public List<BlogResponseDto> getAllBlogs() {
        return blogRepository.findAll().stream()
                .map(this::mapToResponseDto)
                .collect(Collectors.toList());
    }

    public List<BlogResponseDto> getBlogsByUser() {
        UserResponseDto currentUser = userService.getCurrentUser();
        if (currentUser == null) {
            throw new UnauthorizedException("User not authenticated");
        }

        List<Blog> userBlogs = blogRepository.findByUserId(currentUser.getId());
        return userBlogs.stream()
                .map(this::mapToResponseDto)
                .collect(Collectors.toList());
    }

    public BlogResponseDto getBlogById(Long blogId) {
        Blog blog = blogRepository.findById(blogId)
                .orElseThrow(() -> new BlogNotFoundException("Blog not found with id: " + blogId));
        return mapToResponseDto(blog);
    }

    private String saveFile(MultipartFile file) {
        try {
            String uploadDir = "uploads/";
            Path uploadPath = Paths.get(uploadDir);
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }

            String fileName = UUID.randomUUID().toString() + "_" + file.getOriginalFilename();
            Path filePath = uploadPath.resolve(fileName);
            Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

            return "/uploads/" + fileName;
        } catch (IOException e) {
            throw new RuntimeException("File upload failed", e);
        }
    }

    private void deleteFile(String filePath) {
        if (filePath != null && !filePath.isEmpty()) {
            try {
                Path path = Paths.get(filePath.replaceFirst("^/+", ""));
                if (Files.exists(path)) {
                    Files.delete(path);
                }
            } catch (IOException e) {
                throw new RuntimeException("Failed to delete file: " + filePath, e);
            }
        }
    }


    private BlogResponseDto mapToResponseDto(Blog blog) {
        BlogResponseDto responseDto = new BlogResponseDto();
        responseDto.setBlogId(blog.getId());
        responseDto.setTitle(blog.getTitle());
        responseDto.setContent(blog.getContent());
        responseDto.setCreatedBy(blog.getUser().getUsername());
        responseDto.setImageUrl(blog.getImageUrl());
        responseDto.setCreatedAt(blog.getCreatedAt());
        responseDto.setUpdatedAt(blog.getUpdatedAt());
        return responseDto;
    }
}