package com.mf.springboot.springdata.envers.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.mf.springboot.springdata.envers.entity.Comment;
import com.mf.springboot.springdata.envers.entity.Image;
import com.mf.springboot.springdata.envers.entity.Post;
import com.mf.springboot.springdata.envers.entity.User;
import com.mf.springboot.springdata.envers.repository.CommentRepository;
import com.mf.springboot.springdata.envers.repository.ImageRepository;
import com.mf.springboot.springdata.envers.repository.PostRepository;
import com.mf.springboot.springdata.envers.repository.UserRepository;

@Service
public class BlogService {

	@Autowired
	private PostRepository postRepository;
	@Autowired
	private UserRepository userRepository;
	@Autowired
	private CommentRepository commentRepository;
	@Autowired
	private ImageRepository imageRepository;
	
	public List<Post> getAllPosts(){
		return postRepository.findAll();
	}
	
	public Post createNewPost(Post post){
		return postRepository.save(post);
	}
	
	public Post editPost(Integer postID, Post post){
		
		Optional<Post> extPost = postRepository.findById(postID);
		
		if(extPost.isPresent()) {
			extPost.get().setPostTitle(post.getPostTitle() != null ? post.getPostTitle()  : extPost.get().getPostTitle());
			extPost.get().setPostContent(post.getPostContent() != null ? post.getPostContent()  : extPost.get().getPostContent());
			extPost.get().setCategory(post.getCategory() != null ? post.getCategory()  : extPost.get().getCategory());
		}
		
		return extPost.isPresent() ? postRepository.save(extPost.get()) : null;
	}

	public List<Post> getPostEditHistory(Integer postID) {
		
		List<Post> historyList = new ArrayList<Post>();
		
		postRepository.findRevisions(postID).get().forEach(x -> {
			x.getEntity().setEditVersion(x.getMetadata());
			historyList.add(x.getEntity());
		});
		
		return historyList;
	}

	public List<Comment> createComment(Integer postID, Comment comment) {		
		comment.setPostID(postID);
		
		Optional<Post> extPost = postRepository.findById(postID);
		extPost.get().getComments().add(comment);
		postRepository.save(extPost.get());
		
		return postRepository.save(extPost.get()).getComments();
	}

	public List<User> getAllUsers() {
		return userRepository.findAll();
	}

	public User createNewUser(User user) {
		return userRepository.save(user);
	}

	public User editUser(Integer userID, User user){
		
		Optional<User> extUser = userRepository.findById(userID);
		
		if(extUser.isPresent()) {
			extUser.get().setUserName(user.getUserName() != null ? user.getUserName()  : extUser.get().getUserName());
			extUser.get().setPassword(user.getPassword() != null ? user.getPassword()  : extUser.get().getPassword());
		}
		
		return extUser.isPresent() ? userRepository.save(extUser.get()) : null;
	}
	
	public void deleteUser(Integer userID){
		
		Optional<User> extUser = userRepository.findById(userID);
		
		if(extUser.isPresent()) {
			userRepository.delete(extUser.get());
		}
		
	}
	
	public Image addNewImage(Integer postID, Image image) {
		Optional<Post> extPost = postRepository.findById(postID);
		image.setPost(extPost.get());
		
		return imageRepository.save(image);
	}
	
}
