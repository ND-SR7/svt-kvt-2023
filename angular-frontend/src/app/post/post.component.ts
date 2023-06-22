import { Component, OnInit } from '@angular/core';
import { Post } from './model/post.model';
import { Router } from '@angular/router';
import { PostService } from '../post/services/post.service';
import { JwtHelperService } from '@auth0/angular-jwt';
import { UserService } from '../user/services/user.service';
import { Image } from './model/image.model';
import { User } from '../user/model/user.model';
import { Comment } from './model/comment.model';
import { CommentService } from './services/comment.service';

@Component({
  selector: 'app-post',
  templateUrl: './post.component.html',
  styleUrls: ['./post.component.css']
})
export class PostComponent implements OnInit {
  
  post: Post = new Post();
  user: User = new User();
  images: Image[] = [];

  comments: Comment[] = [];
  replies: Comment[] = [];
  
  users: Map<number, User> = new Map();

  constructor(
    private postService: PostService,
    private commentService: CommentService,
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
          },
          error => {
            window.alert('Error while retriving post\'s user');
            console.log(error);
          }
        );

        this.postService.getImages(this.post.id).subscribe(
          result => {
            this.images = result.body as Image[];
          },
          error => {
            window.alert('Error while retriving images for post');
            console.log(error);
          }
        );

        this.postService.getComments(this.post.id).subscribe(
          result => {
            let temp: Comment[] = result.body as Comment[];

            temp.forEach(comment => {
              if (comment.repliesToCommentId != null)
                this.replies.push(comment);
              else
                this.comments.push(comment);
            });

            this.comments.forEach(comment => {
              this.userService.getOne(comment.belongsToUserId).subscribe(
                result => {
                  let user: User = result.body as User;
                  this.users.set(user.id, user);
                }
              )
            });

            this.replies.forEach(comment => {
              this.userService.getOne(comment.belongsToUserId).subscribe(
                result => {
                  let user: User = result.body as User;
                  this.users.set(user.id, user);
                }
              )
            });
          },
          error => {
            window.alert('Error while retriving comments for post');
            console.log(error);
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

  canReply(id: number): boolean {
    const commentDiv = document.getElementById('comment' + id);
    const replyDiv = commentDiv?.querySelector('div.indented');
    if (replyDiv) {
      return false;
    } else {
      return true;
    }
  }

  reply(commentId: number) {
    let reply: Comment = new Comment();
    
    let text = prompt('Enter your reply:');
    if (text == null || text == "") {
      return;
    } else {
      reply.text = text;
    }

    reply.timestamp = new Date().toISOString().slice(0, 10);

    reply.belongsToPostId = this.post.id;
    reply.repliesToCommentId = commentId;

    let sub: string;
    const item = localStorage.getItem('user') || "";

    const jwt: JwtHelperService = new JwtHelperService();
    const decodedToken = jwt.decodeToken(item);
    sub = decodedToken.sub;

    this.userService.getOneByUsername(sub).subscribe(
      result => {
        const user: User = result.body as User;
        reply.belongsToUserId = user.id;

        this.commentService.add(reply).subscribe(
          result => {
            window.alert('Replied to comment');
            location.reload();
          },
          error => {
            window.alert('Error while replying to comment');
            console.log(error);
          }
        );
      }
    );
  }

  addComment(postId: number) {
    let comment: Comment = new Comment();
    
    let text = prompt('Enter your comment:');
    if (text == null || text == "") {
      return;
    } else {
      comment.text = text;
    }

    comment.timestamp = new Date().toISOString().slice(0, 10);

    comment.belongsToPostId = this.post.id;

    let sub: string;
    const item = localStorage.getItem('user') || "";

    const jwt: JwtHelperService = new JwtHelperService();
    const decodedToken = jwt.decodeToken(item);
    sub = decodedToken.sub;

    this.userService.getOneByUsername(sub).subscribe(
      result => {
        const user: User = result.body as User;
        comment.belongsToUserId = user.id;

        this.commentService.add(comment).subscribe(
          result => {
            window.alert('Successfully added a comment');
            location.reload();
          },
          error => {
            window.alert('Error while adding a comment');
            console.log(error);
          }
        );
      }
    );
  }

  canDeleteComment(): boolean {
    let sub: string;
    let role: string;
    const item = localStorage.getItem('user');
    let canDelete: boolean = false;

    if (!item) {
			this.router.navigate(['login']);
			return false;
		}

    const jwt: JwtHelperService = new JwtHelperService();
    const decodedToken = jwt.decodeToken(item);
    role = decodedToken.role.authority;
    sub = decodedToken.sub;
    
    this.comments.forEach(comment => {
      if (this.users.get(comment.belongsToUserId)?.username == sub)
        canDelete = true;
    });

    if (role == 'ROLE_ADMIN')
      canDelete = true;
    
    return canDelete;
  }

  canDeleteReply(): boolean {
    let sub: string;
    let role: string;
    const item = localStorage.getItem('user');
    let canDelete: boolean = false;

    if (!item) {
			this.router.navigate(['login']);
			return false;
		}

    const jwt: JwtHelperService = new JwtHelperService();
    const decodedToken = jwt.decodeToken(item);
    role = decodedToken.role.authority;
    sub = decodedToken.sub;
    
    this.replies.forEach(reply => {
      if (this.users.get(reply.belongsToUserId)?.username == sub)
        canDelete = true;
    });

    if (role == 'ROLE_ADMIN')
      canDelete = true;
    
    return canDelete;
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

  deleteComment(id: number) {
    this.commentService.delete(id).subscribe(
      result => {
        window.alert('Successfully deleted comment!');
        location.reload();
      },
      error => {
        window.alert('Error while deleting comment');
        console.log(error);
      }
    );
  }
}
