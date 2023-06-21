import { Component, OnInit } from '@angular/core';
import { Group } from './model/group.model';
import { Post } from '../post/model/post.model';
import { GroupService } from './services/group.service';
import { Router } from '@angular/router';
import { PostService } from '../post/services/post.service';
import { JwtHelperService } from '@auth0/angular-jwt';
import { UserService } from '../user/services/user.service';
import { User } from '../user/model/user.model';

@Component({
  selector: 'app-group',
  templateUrl: './group.component.html',
  styleUrls: ['./group.component.css']
})
export class GroupComponent implements OnInit{

  group: Group = new Group();
  posts: Post[] = [];
  users: Map<number, User> = new Map();
  canPost: boolean = false;

  constructor(
    private groupService: GroupService,
    private postService: PostService,
    private userService: UserService,
    private router: Router
  ) { }

  ngOnInit(): void {
    const url: String = this.router.url;
    const id: number = Number.parseInt(url.split('/')[2]);

    this.groupService.getOne(id).subscribe(
      result => {
        this.group = result.body as Group;
      }
    );

    this.postService.getAllForGroup(id).subscribe(
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

    this.groupService.checkUserInGroup(id).subscribe(
      result => {
        const status = result.status;
        if (status == 200) {
          console.log(this.canPost);
        }
        this.canPost = true;
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
    
    if (role == "ROLE_ADMIN") //todo ako brise admin grupe
      return true;
    return false;
  }

  deleteGroup() {
    this.groupService.delete(this.group.id);
  }
}
