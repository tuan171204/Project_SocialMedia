package com.example.project_socialmedia.domain.Repository;

import com.example.project_socialmedia.domain.Model.Post;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PostRepository extends JpaRepository<Post, Long> {
}
