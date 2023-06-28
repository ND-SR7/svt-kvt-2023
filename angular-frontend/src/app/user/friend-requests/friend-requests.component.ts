import { Component, OnInit } from '@angular/core';
import { FriendRequest } from '../model/friendRequest.model';
import { User } from '../model/user.model';
import { JwtHelperService } from '@auth0/angular-jwt';
import { UserService } from '../services/user.service';

@Component({
  selector: 'app-friend-requests',
  templateUrl: './friend-requests.component.html',
  styleUrls: ['./friend-requests.component.css']
})
export class FriendRequestsComponent implements OnInit {

  user: User = new User();
  friendRequests: FriendRequest[] = [];
  sentRequests: FriendRequest[] = [];
  recievedRequests: FriendRequest[] = [];

  requestUsers: Map<number, User> = new Map(); //id korisnika i korisnik za template

  constructor(
    private userService: UserService
  ) { }
  
  ngOnInit(): void {
    const item: string = localStorage.getItem('user') || '';
    const jwt: JwtHelperService = new JwtHelperService();
    const sub: string = jwt.decodeToken(item).sub;

    this.userService.getOneByUsername(sub).subscribe(
      result => {
        this.user = result.body as User;

        this.userService.getFriendRequests().subscribe(
          result => {
            this.friendRequests = result.body as FriendRequest[];

            this.friendRequests.forEach(request => {
              if (request.fromUserId == this.user.id)
                this.sentRequests.push(request);
              else if (request.toUserId == this.user.id)
                this.recievedRequests.push(request);
            });

            this.sentRequests.forEach(request => {
                this.userService.getOne(request.toUserId).subscribe(
                  result => {
                    const user: User = result.body as User;
                    this.requestUsers.set(user.id, user);
                  }
                );
            });

            this.recievedRequests.forEach(request => {
              this.userService.getOne(request.fromUserId).subscribe(
                result => {
                  const user: User = result.body as User;
                  this.requestUsers.set(user.id, user);
                }
              );
            });
          }
        );
      }
    );
  }
}
