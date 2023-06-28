import { Component, OnInit } from '@angular/core';
import { User } from '../model/user.model';
import { UserService } from '../services/user.service';
import { JwtHelperService } from '@auth0/angular-jwt';
import { UserQuery } from '../model/userQuery.model';
import { Image } from 'src/app/post/model/image.model';
import { FriendRequest } from '../model/friendRequest.model';

@Component({
  selector: 'app-friends',
  templateUrl: './friends.component.html',
  styleUrls: ['./friends.component.css']
})
export class FriendsComponent implements OnInit {

  user: User = new User();
  friends: User[] = [];
  foundUsers: User[] = [];

  images: Map<number, Image> = new Map();

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
      }
    );

    this.userService.getUserFriends().subscribe(
      result => {
        this.friends = result.body as User[];

        this.friends.forEach(friend => {
          this.userService.getProfileImage(friend.id).subscribe(
            result => {
              if (result.body != null) {
                const image: Image = result.body as Image;
                this.images.set(friend.id, image);
              }
            },
            error => {
              window.alert('Error while retriving profile image for friend ' + friend.username);
              console.log(error);
            }
          );
        });
      },
      error => {
        window.alert('Error while retriving user\'s friends');
        console.log(error);
      }
    );
  }

  findUsers(): void {
    const input: HTMLInputElement = document.getElementById('user-search') as HTMLInputElement;
    const query: string[] = input.value.split(' ') || ['', ''];
    const name1: string = query[0];
    const name2: string = query[1];

    this.userService.searchUsers(new UserQuery({name1, name2})).subscribe(
      result => {
        this.foundUsers = result.body as User[];
        //ako pronadje ulogovanog korisnika da ga izbaci iz rezultata
        this.foundUsers = this.foundUsers.filter(foundUser => foundUser.username != this.user.username);

        this.foundUsers.forEach(foundUser => {
          this.userService.getProfileImage(foundUser.id).subscribe(
            result => {
              if (result.body != null) {
                const image: Image = result.body as Image;
                this.images.set(foundUser.id, image);
              }
            },
            error => {
              window.alert('Error while retriving profile image for found user ' + foundUser.username);
              console.log(error);
            }
          );
        });
      },
      error => {
        window.alert('Error while searching for users');
        console.log(error);
      }
    );
  }

  checkIfFriend(userId: number): boolean {
    let isFriend: boolean = false;
    this.friends.forEach(friend => {
      if (friend.id == userId)
        isFriend = true;
    });
    return isFriend;
  }

  sendFriendRequest(userId: number): void {
    const friendRequest: FriendRequest = new FriendRequest();
    friendRequest.fromUserId = this.user.id;
    friendRequest.toUserId = userId;
    friendRequest.createdAt = new Date().toISOString().slice(0, -1);

    this.userService.sendFriendRequest(friendRequest).subscribe(
      result => {
        window.alert('Successfully sent friend request');
      }
    );
  }
}
