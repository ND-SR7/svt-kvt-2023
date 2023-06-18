import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { Router } from '@angular/router';
import { AuthenticationService } from 'src/app/user/services/authentication.service';
import { Post } from '../model/post.model';
import { PostService } from '../services/post.service';

@Component({
  selector: 'app-add-post',
  templateUrl: './add-post.component.html',
  styleUrls: ['./add-post.component.css']
})
export class AddPostComponent implements OnInit{

  form: FormGroup;

  constructor(
    private fb: FormBuilder,
    private authenticatioService: AuthenticationService,
    private router: Router,
    private postService: PostService
  ) {
    this.form = this.fb.group({
      content: [null, Validators.required],
      creationDate: [null, Validators.required],
      postedByUserId: [null, Validators.required]
    })
  }

  ngOnInit(): void {
    
  }

  submit() {
    const post: Post = new Post;
		post.content = this.form.value.content;
    post.creationDate = this.form.value.creationDate;
    post.postedByUserId = this.form.value.postedByUserId;
		
    this.postService.add(post).subscribe(
			result => {
				window.alert('Successfully added a post!');
				this.router.navigate(['posts']);
			},
			error => {
        window.alert('An error occurred!');
				console.log(error);
			}
		);
  }
}
