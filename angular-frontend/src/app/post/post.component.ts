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
import { Reaction } from './model/reaction.model';
import { ReactionService } from './services/reaction.service';
import { ToastrService } from 'ngx-toastr';

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
  reactions: Reaction[] = [];

  likes: number = 0;
  hearts: number = 0;
  dislikes: number = 0;

  commentsReactions: Map<number, Map<number, Reaction>> = new Map(); //Map<idKomentara, Map<idReakcije, Reakcija>> zbog brisanja reakcija na komentar
  
  users: Map<number, User> = new Map();

  constructor(
    private postService: PostService,
    private commentService: CommentService,
    private userService: UserService,
    private reactionService: ReactionService,
    private toastr: ToastrService,
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
            this.toastr.error('Error while retriving post\'s user');
            console.log(error);
          }
        );

        this.postService.getImages(this.post.id).subscribe(
          result => {
            this.images = result.body as Image[];
          },
          error => {
            this.toastr.error('Error while retriving images for post');
            console.log(error);
          }
        );

        this.reactionService.getReactionsForPost(this.post.id).subscribe(
          result => {
            this.reactions = result.body as Reaction[];
            
            this.reactions.forEach(reaction => {
              if (reaction.reactionType == 'LIKE')
                this.likes++;
              else if (reaction.reactionType == 'HEART')
                this.hearts++;
              else if (reaction.reactionType == 'DISLIKE')
                this.dislikes++;
            });
          },
          error => {
            this.toastr.error('Error while retriving reactions for post');
            console.log(error);
          }
        );

        this.postService.getComments(this.post.id).subscribe(
          result => {
            let temp: Comment[] = result.body as unknown as Comment[];

            temp.forEach(comment => {
              if (comment.repliesToCommentId != null)
                this.replies.push(comment);
              else
                this.comments.push(comment);
            });

            temp.forEach(comment => {
              this.reactionService.getReactionsForComment(comment.id).subscribe(
                result => {
                  const reactions: Reaction[] = result.body as Reaction[];
                  const reactionsMap: Map<number, Reaction> = new Map();
                  reactions.forEach(reaction => {
                    reactionsMap.set(reaction.id, reaction);
                  });
                  this.commentsReactions.set(comment.id, reactionsMap);
                },
                error => {
                  this.toastr.error('Error while retriving reactions to comment ' + comment.id);
                  console.log(error);
                }
              );
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
            this.toastr.error('Error while retriving comments for post');
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
            this.toastr.success('Replied to comment');
            location.reload();
          },
          error => {
            this.toastr.error('Error while replying to comment');
            console.log(error);
          }
        );
      }
    );
  }

  addComment() {
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
            this.toastr.success('Successfully added a comment');
            location.reload();
          },
          error => {
            this.toastr.error('Error while adding a comment');
            console.log(error);
          }
        );
      }
    );
  }

  canDeleteComment(commentId: number): boolean {
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
      if (this.users.get(comment.belongsToUserId)?.username == sub && comment.id == commentId)
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
        this.toastr.success('Successfully deleted the post!');
        this.router.navigate(['posts']);
      },
      error => {
        this.toastr.error('Error while deleting post');
        console.log(error);
      }
    );
  }

  deleteComment(id: number) {
    this.commentService.delete(id).subscribe(
      result => {
        this.toastr.success('Successfully deleted comment!');
        location.reload();
      },
      error => {
        this.toastr.error('Error while deleting comment');
        console.log(error);
      }
    );
  }

  getReactionCount(commentId: number, reactionType: string): number {
    const reactions: Reaction[] = [];
    this.commentsReactions.get(commentId)?.forEach(reaction => {
      reactions.push(reaction);
    });
    let reactionCount: number = 0;

    reactions.forEach(reaction => {
      if (reaction.reactionType == reactionType)
        reactionCount++;
    });

    return reactionCount;
  }

  async reactedPost(postId: number, reactionType: string) {
    let reactorId: number = (await this.userService.extractUser() as User).id;
    let reaction = this.reactions.find(
      reaction => reaction.onPostId == postId && reaction.reactionType == reactionType && reaction.madeByUserId == reactorId);
    let previousReaction = this.reactions.find(reaction => reaction.onPostId == postId && reaction.madeByUserId == reactorId);
    
    if (reaction === undefined && previousReaction !== undefined) {
      this.reactionService.delete(previousReaction.id).subscribe(
        result => {
          if (reactionType == 'LIKE')
            this.likes--;
          else if (reactionType == 'HEART')
            this.hearts--;
          else if (reactionType == 'DISLIKE')
            this.dislikes--;
        },
        error => {
          this.toastr.error('Error while removing reaction on post ' + postId);
          console.log(error);
        }
      );

      reaction = new Reaction();
      reaction.reactionType = reactionType;
      reaction.timestamp = new Date().toISOString().slice(0, 10);
      reaction.madeByUserId = reactorId;
      reaction.onPostId = postId;

      this.reactionService.add(reaction).subscribe(
        result => {
          if (reactionType == 'LIKE')
            this.likes++;
          else if (reactionType == 'HEART')
            this.hearts++;
          else if (reactionType == 'DISLIKE')
            this.dislikes++;
          location.reload();
        },
        error => {
          this.toastr.error('Error while reacting on post ' + postId);
          console.log(error);
        }
      );

    } else if (reaction === undefined) {
      reaction = new Reaction();
      reaction.reactionType = reactionType;
      reaction.timestamp = new Date().toISOString().slice(0, 10);
      reaction.madeByUserId = reactorId;
      reaction.onPostId = postId;

      this.reactionService.add(reaction).subscribe(
        result => {
          if (reactionType == 'LIKE')
            this.likes++;
          else if (reactionType == 'HEART')
            this.hearts++;
          else if (reactionType == 'DISLIKE')
            this.dislikes++;
          location.reload();
        },
        error => {
          this.toastr.error('Error while reacting on post ' + postId);
          console.log(error);
        }
      );
    } else {
      this.reactionService.delete(reaction.id).subscribe(
        result => {
          if (reactionType == 'LIKE')
            this.likes--;
          else if (reactionType == 'HEART')
            this.hearts--;
          else if (reactionType == 'DISLIKE')
            this.dislikes--;
          location.reload();
        },
        error => {
          this.toastr.error('Error while removing reaction on post ' + postId);
          console.log(error);
        }
      );
    }
  }

  async reactedComment(commentId: number, reactionType: string) {
    let reactorId: number = (await this.userService.extractUser() as User).id;
    let reaction: Reaction = new Reaction();
    let previousReaction: Reaction = new Reaction();

    this.commentsReactions.get(commentId)?.forEach(temp => {
      if (temp.onCommentId == commentId && temp.reactionType == reactionType && temp.madeByUserId == reactorId)
        reaction = temp;
    });

    this.commentsReactions.get(commentId)?.forEach(temp => {
      if (temp.onCommentId == commentId && temp.madeByUserId == reactorId)
        previousReaction = temp;
    });
    
    if (reaction.id == null && previousReaction.id != null) {
      this.reactionService.delete(previousReaction.id).subscribe(
        result => {
          this.commentsReactions.get(commentId)?.delete(previousReaction.id);
        },
        error => {
          this.toastr.error('Error while removing reaction on comment ' + commentId);
          console.log(error);
        }
      );

      reaction = new Reaction();
      reaction.reactionType = reactionType;
      reaction.timestamp = new Date().toISOString().slice(0, 10);
      reaction.madeByUserId = reactorId;
      reaction.onCommentId = commentId;

      this.reactionService.add(reaction).subscribe(
        result => {
          this.commentsReactions.get(commentId)?.set(reaction.id, reaction);
          location.reload();
        },
        error => {
          this.toastr.error('Error while reacting on comment ' + commentId);
          console.log(error);
        }
      );

    } else if (reaction.id == null) {
      reaction = new Reaction();
      reaction.reactionType = reactionType;
      reaction.timestamp = new Date().toISOString().slice(0, 10);
      reaction.madeByUserId = reactorId;
      reaction.onCommentId = commentId;

      this.reactionService.add(reaction).subscribe(
        result => {
          this.commentsReactions.get(commentId)?.set(reaction.id, reaction);
          location.reload();
        },
        error => {
          this.toastr.error('Error while reacting on comment ' + commentId);
          console.log(error);
        }
      );

    } else {
      this.reactionService.delete(reaction.id).subscribe(
        result => {
          this.commentsReactions.get(commentId)?.delete(previousReaction.id);
          location.reload();
        },
        error => {
          this.toastr.error('Error while removing reaction on comment ' + commentId);
          console.log(error);
        }
      );
    }
  }
}
