import { Component, OnInit } from '@angular/core';
import { Group } from './model/group.model';
import { Post } from '../post/model/post.model';
import { GroupService } from './services/group.service';
import { Router } from '@angular/router';
import { PostService } from '../post/services/post.service';
import { JwtHelperService } from '@auth0/angular-jwt';

@Component({
  selector: 'app-group',
  templateUrl: './group.component.html',
  styleUrls: ['./group.component.css']
})
export class GroupComponent implements OnInit{

  group: Group = new Group();
  posts: Post[] = [];

  constructor(
    private groupService: GroupService,
    private postService: PostService,
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

    // this.postService.getAllForGroup(id).subscribe(
    //   result => {
    //     this.posts = result.body as Post[];
    //   }
    // );
  }

  hasAuthority(): boolean {
    let role: string;
    const item = localStorage.getItem('user');

    if (!item) {
			this.router.navigate(['login']);
			role = "";
			return false;
		}

    const jwt: JwtHelperService = new JwtHelperService();
		role = jwt.decodeToken(item).role.authority;
    
    if (role == "ROLE_ADMIN")
      return true;
    return false;
  }

  deleteGroup() {
    this.groupService.delete(this.group.id);
  }
}
