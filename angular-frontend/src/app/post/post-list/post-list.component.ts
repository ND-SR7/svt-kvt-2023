import { Component, OnInit } from '@angular/core';
import { Post } from '../model/post.model';
import { PostService } from '../services/post.service';
import { Router } from '@angular/router';

@Component({
  selector: 'app-post-list',
  templateUrl: './post-list.component.html',
  styleUrls: ['./post-list.component.css']
})
export class PostListComponent implements OnInit{

  posts: Post[] = [];

  constructor(
    private postService: PostService,
    private router: Router
  ) { }

  ngOnInit(): void {
    this.postService.getAll().subscribe(
      result => {
        this.posts = result.body as Post[];
      }
    );
  }
}
