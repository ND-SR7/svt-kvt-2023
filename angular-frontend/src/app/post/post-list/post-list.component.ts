import { Component, OnInit } from '@angular/core';
import { Post } from '../model/post.model';
import { PostService } from '../services/post.service';
import { Router } from '@angular/router';
import { UserService } from '../../user/services/user.service';
import { User } from '../../user/model/user.model'

@Component({
  selector: 'app-post-list',
  templateUrl: './post-list.component.html',
  styleUrls: ['./post-list.component.css']
})
export class PostListComponent implements OnInit{

  posts: Post[] = [];
  users: Map<number, User> = new Map();

  constructor(
    private postService: PostService,
    private router: Router,
    private userService: UserService,
  ) { }

  ngOnInit(): void {
    this.postService.getHomepagePosts().subscribe(
      result => {
        this.posts = result.body as Post[];
        
        this.posts.forEach(post => {
          this.userService.getOne(post.postedByUserId).subscribe(
            result => {
              let user: User = result.body as User;
              this.users.set(user.id, user);
            }
          )
        });
      }
    );
  }
}
