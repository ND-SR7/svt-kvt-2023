import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { JwtHelperService } from '@auth0/angular-jwt';
import { Router } from '@angular/router';
import { Post } from '../model/post.model';
import { PostService } from '../services/post.service';
import { UserService } from 'src/app/user/services/user.service';
import { User } from 'src/app/user/model/user.model';
import { Image } from '../model/image.model';

@Component({
  selector: 'app-add-edit-post',
  templateUrl: './add-edit-post.component.html',
  styleUrls: ['./add-edit-post.component.css']
})
export class AddEditPostComponent implements OnInit{

  form: FormGroup;
  editing: boolean = this.router.url.includes('edit');
  postForGroup: number = Number.parseInt(this.router.url.split('/')[2]) || 0;
  imagePaths: string[] = [];
  images: Image[] = [];

  constructor(
    private fb: FormBuilder,
    private router: Router,
    private postService: PostService,
    private userService: UserService
  ) {
    this.form = this.fb.group({
      content: [null, Validators.required],
      images: [null, Validators.nullValidator]
    });

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

  onFileChange(event: Event) {
    const inputElement = event.target as HTMLInputElement;
    const files: FileList | null = inputElement.files;
    this.imagePaths = [];

    if (files) {
      for (let i = 0; i < files.length; i++) {
        const file: File = files[i];
        this.imagePaths.push(file.name);
      }
    }
  }

  submit() {
    if (!this.editing) {
      const post: Post = new Post();
      post.content = this.form.value.content;
      post.creationDate = new Date().toISOString().slice(0, -1);
      
      const jwt: JwtHelperService = new JwtHelperService();
      const userToken: string = localStorage.getItem('user') || '';
		  const decoded = jwt.decodeToken(userToken);

      this.userService.getOneByUsername(decoded.sub).subscribe(
        result => {
          let user: User = result.body as User;
          post.postedByUserId = user.id;

          if (this.imagePaths.length > 0) {
            this.imagePaths.forEach((imageName: string) => {
              let image: Image = new Image();
              image.path = '../../assets/images/' + imageName;
              image.belongsToPostId = post.id;
              this.images.push(image);
            });
          }

          post.images = this.images;

          if (this.postForGroup > 0)
            post.belongsToGroupId = this.postForGroup;

          this.postService.add(post).subscribe(
            result => {
              window.alert('Successfully added a post!');
              if (this.postForGroup > 0)
                this.router.navigate(['groups/' + this.postForGroup]);
              else
                this.router.navigate(['posts']);
            },
            error => {
              window.alert('An error occurred adding a post!');
              console.log(error);
            }
          );
        }
      );
    } else {
      const post: Post = new Post({_id: Number.parseInt(this.router.url.split('/')[3])});
      post.content = this.form.value.content;

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
