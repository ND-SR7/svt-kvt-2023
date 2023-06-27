import { Component, OnInit } from '@angular/core';
import { User } from '../model/user.model';
import { UserService } from '../services/user.service';
import { JwtHelperService } from '@auth0/angular-jwt';

@Component({
  selector: 'app-friends',
  templateUrl: './friends.component.html',
  styleUrls: ['./friends.component.css']
})
export class FriendsComponent implements OnInit {

  user: User = new User();
  friends: User[] = [];

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

    this.userService.getUserFriends(this.user.id).subscribe(
      result => {
        this.friends = result.body as User[];
      },
      error => {
        window.alert('Error while retriving user\'s friends');
        console.log(error);
      }
    );
  }

}
