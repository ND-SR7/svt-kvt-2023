import { Component, OnInit } from '@angular/core';
import { Post } from './model/post.model';
import { Router } from '@angular/router';
import { PostService } from '../post/services/post.service';
import { JwtHelperService } from '@auth0/angular-jwt';
import { UserService } from '../user/services/user.service';
import { User } from '../user/model/user.model';

@Component({
  selector: 'app-post',
  templateUrl: './post.component.html',
  styleUrls: ['./post.component.css']
})
export class PostComponent implements OnInit{
  
  post: Post = new Post();
  user: User = new User();

  constructor(
    private postService: PostService,
    private userService: UserService,
    private router: Router
  ) { }
  
  ngOnInit(): void {
    const url: String = this.router.url;
    const id: number = Number.parseInt(url.split('/')[2]);

    this.postService.getOne(id).subscribe(
      result => {
        this.post = result.body as Post;

        this.userService.getOne(this.post.postedByUserId).subscribe(
          result => {
            this.user = result.body as User;
          }
        );
      }
    );
  }

  hasAuthority(): boolean {
    let role: string;
    let sub: string;
    const item = localStorage.getItem('user');

    if (!item) {
			this.router.navigate(['login']);
			role = "";
			return false;
		}

    const jwt: JwtHelperService = new JwtHelperService();
    const decodedToken = jwt.decodeToken(item);
		role = decodedToken.role.authority;
    sub = decodedToken.sub;
    
    if (role == 'ROLE_ADMIN' || sub == this.user.username)
      return true;
    return false;
  }

  deletePost() {
    this.postService.delete(this.post.id).subscribe(
      result => {
        window.alert('Successfully deleted the post!');
        this.router.navigate(['posts']);
      },
      error => {
        window.alert('Error while deleting post');
        console.log(error);
      }
    );
  }
}
