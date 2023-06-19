import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { Router } from '@angular/router';
import { Post } from '../model/post.model';
import { PostService } from '../services/post.service';

@Component({
  selector: 'app-add-edit-post',
  templateUrl: './add-edit-post.component.html',
  styleUrls: ['./add-edit-post.component.css']
})
export class AddEditPostComponent implements OnInit{

  form: FormGroup;
  editing: boolean = this.router.url.includes('edit');

  constructor(
    private fb: FormBuilder,
    private router: Router,
    private postService: PostService
  ) {
    this.form = this.fb.group({
      content: [null, Validators.required],
      creationDate: [null, Validators.required],
      postedByUserId: [null, Validators.required]
    })

    if (this.editing) {
      const id: number = Number.parseInt(this.router.url.split('/')[3]);
      this.postService.getOne(id).subscribe(
        result => {
          const post: Post = result.body as Post;
          this.form.patchValue(post);
        },
        error => {
          window.alert('An error occurred retriving post!');
          console.log(error);
        }
      );
    }
  }

  ngOnInit(): void {
    
  }

  submit() {
    if (!this.editing) {
      const post: Post = new Post();
      post.content = this.form.value.content;
      post.creationDate = this.form.value.creationDate;
      post.postedByUserId = this.form.value.postedByUserId;
      
      this.postService.add(post).subscribe(
        result => {
          window.alert('Successfully added a post!');
          this.router.navigate(['posts']);
        },
        error => {
          window.alert('An error occurred adding a post!');
          console.log(error);
        }
      );
    } else {
      const post: Post = new Post({_id: Number.parseInt(this.router.url.split('/')[3])});
      post.content = this.form.value.content;
      post.creationDate = this.form.value.creationDate;
      post.postedByUserId = this.form.value.postedByUserId;

      this.postService.edit(post).subscribe(
        result => {
          window.alert('Successfully edited the post!');
          this.router.navigate(['posts']);
        },
        error => {
          window.alert('An error occurred editing the post!');
          console.log(error);
        }
      );
    }
  }
}
